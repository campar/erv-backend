package com.example.erv.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.example.erv.exceptions.ErvException;
import com.example.erv.repository.GenericRepository;

public abstract class GenericService<T> {
    private final GenericRepository<T> repository;

    protected GenericService(GenericRepository<T> repository) {
        this.repository = repository;
    }

    public List<T> findAll() {
        return repository.findAll();
    }
    
    public abstract List<?> findAllMinimal();
    
    public Page<T> findAll(Pageable pageable) {
    	Pageable sortedPageable = withDefaultSort(pageable);
        return repository.findAll(sortedPageable);
    }

    public List<T> findAll(Specification<T> specification) {
        return repository.findAll(specification);
    }

    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
    	Pageable sortedPageable = withDefaultSort(pageable);
        return repository.findAll(specification, sortedPageable);
    }

    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    public abstract T insert(T object) throws ErvException;

    public abstract T update(T object) throws ErvException;

    public T save(T object) {
        return repository.save(object);
    }

    public void deleteById(Long id) throws ErvException {
        repository.deleteById(id);
    }

    public abstract Class<T> getEntityClass();

    public List<T> saveAll(List<T> objects) {
        return repository.saveAll(objects);
    }

    public long count(Specification<T> specification) {
        return repository.count(specification);
    }
    
    private Pageable withDefaultSort(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        }
        return pageable;
    }
}
