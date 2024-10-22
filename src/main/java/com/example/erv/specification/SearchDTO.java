package com.example.erv.specification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.example.erv.config.AppConfig;
import com.example.erv.exceptions.FieldNotFoundException;
import com.example.erv.specification.deserializers.PageSizeDeserializer;
import com.example.erv.utils.ApiUtil;
import com.example.erv.utils.CustomSortDeserializer;
import com.example.erv.utils.CustomSortSerializer;
import com.example.erv.utils.LocalDateParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Usage in the REST JSON request:
 * <br>
 *
 * <pre>
 * {
 *   "pageNumber": {@link Integer},
 *   "pageSize": {@link Integer},
 *   "sort": [{@link String} array],
 *   "filterGroup": {
 *     "logicalOperator": {@link LogicalOperator},
 *     "filters": [
 *       {
 *         "key": name of the field {@link String},
 *         "searchOperator": {@link SearchOperator},
 *         "value": any object corresponding to field
 *       },
 *       {
 *         "logicalOperator": {@link LogicalOperator},
 *         "filters": [
 *           {
 *             "key": name of the field {@link String},
 *             "searchOperator": {@link SearchOperator},
 *             "value": any object corresponding to field
 *           }
 *         ]
 *       }
 *     ]
 *   }
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class SearchDTO {
    private static final Logger logger = LogManager.getLogger(SearchDTO.class);
    private static final List<String> FORBIDDEN_FILTER_KEYS = List.of("password");

    private Integer pageNumber;
    @JsonDeserialize(using = PageSizeDeserializer.class)
    private Integer pageSize;
    private Sort sort;
    private FilterGroup filterGroup;
    @JsonIgnore
    private Class<?> clazz;

    /**
     * @return Pageable object for pagination and sorting based on the request.
     */
    public Pageable createPageable() {
        int number = (getPageNumber() != null) ? getPageNumber() : ApiUtil.DEFAULT_PAGE;
        int size = (getPageSize() != null) ? getPageSize() : ApiUtil.DEFAULT_SIZE;
        Sort sort = (getSort() != null && !getSort().isEmpty()) ? getSort() : Sort.unsorted();
        return PageRequest.of(number, size, sort);
    }

    /**
     * Builds a Specification object for filtering data based on the request.
     *
     * @param clazz the entity class for which the search request is made
     * @return a wrapped Specification object for filtering data based on the
     * request
     * @throws FieldNotFoundException if keys of the filter request do not
     *                                correspond to fields of the {@code clazz}
     *                                entity
     */
    public <T> SpecificationBasic<T> buildSpecification(Class<T> clazz) throws FieldNotFoundException {
        this.clazz = clazz;
        SpecificationBasic<T> specification = SpecificationBasic.get();
        checkFilters(clazz);
        parseFilters(filterGroup.getFilters(), specification, filterGroup.getLogicalOperator());
        return specification;
    }

    /**
     * Builds a specification structure with possible nested filters and
     * combinations of logical operators.
     */
    private void parseFilters(List<SearchFilter> filters, SpecificationBasic<?> specification, LogicalOperator logicalOperator) {
        for (SearchFilter filter : filters) {
            if (filter.getFilters() != null && !filter.getFilters().isEmpty()) {
                parseFilters(filter.getFilters(), specification, filter.getLogicalOperator());
            } else {
                if (logicalOperator.equals(LogicalOperator.OR)) {
                    specification.or(filter::getKey, filter.getSearchOperator(), convertFilterValue(filter));
                } else if (logicalOperator.equals(LogicalOperator.AND)) {
                    specification.and(filter::getKey, filter.getSearchOperator(), convertFilterValue(filter));
                }
            }
        }
    }

    /**
     * Checks if the filter key is a field of the model entity.
     *
     * @param clazz Model entity
     * @throws FieldNotFoundException if the field is not found in the model entity.
     */
    protected void checkFilters(Class<?> clazz) throws FieldNotFoundException {
        List<String> fields = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).toList();
        for (SearchFilter filter : filterGroup.getFilters()) {
            if (FORBIDDEN_FILTER_KEYS.contains(filter.getKey())) {
                throw new FieldNotFoundException(filter.getKey());
            }
            if ((filter.getFilters() == null || filter.getFilters().isEmpty()) && !fields.contains(filter.getKey()))
                throw new FieldNotFoundException(filter.getKey());
        }
    }

    public Object convertFilterValue(SearchFilter filter) {
        try {
            String key = filter.getKey();
            Object value = filter.getValue();

            Field field = clazz.getDeclaredField(key);
            Class<?> fieldType = field.getType();

            return castValueToType(value, fieldType);
        } catch (NoSuchFieldException | SecurityException e) {
            logger.error(e);
        }
        return filter.getValue();
    }

    private Object castValueToType(Object value, Class<?> type) {
        if (type.equals(Instant.class)) {
            if (value instanceof ArrayList<?> between) {
                Instant from = parseInstant((String) between.get(0));
                Instant to = parseInstant((String) between.get(1));
                return List.of(from, to);
            }
            return parseInstant((String) value);
        } else if (type.equals(ZonedDateTime.class)) {
            if (value instanceof ArrayList<?> between) {
                ZonedDateTime from = parseZonedDateTime((String) between.get(0));
                ZonedDateTime to = parseZonedDateTime((String) between.get(1));
                return List.of(from, to);
            }
            return parseZonedDateTime((String) value);
        } else if (type.equals(LocalDate.class)) {
            return LocalDateParser.parse((String) value);
        } else if (type.equals(String.class)) {
            return value.toString();
        } else if (type.equals(Integer.class)) {
            return Integer.valueOf((String) value);
        } else if (type.equals(Long.class)) {
            return Long.valueOf((String) value);
        } else if (type.equals(Date.class)) {
            return parseDate(value.toString());
        }
        return value;
    }

    private Instant parseInstant(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ZonedDateTime.of(localDateTime, ZoneId.of(AppConfig.getTimezoneId())).toInstant();
    }

    private ZonedDateTime parseZonedDateTime(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ZonedDateTime.of(localDateTime, ZoneId.of(AppConfig.getTimezoneId()));
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.ENGLISH);
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            logger.error(e);
        }
        return null;
    }

    @JsonSerialize(using = CustomSortSerializer.class)
    @JsonDeserialize(using = CustomSortDeserializer.class)
    @SuppressWarnings("unused")
    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
