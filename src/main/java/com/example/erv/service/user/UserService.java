package com.example.erv.service.user;

import com.example.erv.config.AppConfig;
import com.example.erv.dto.PasswordChangeDTO;
import com.example.erv.dto.PasswordMismatchException;
import com.example.erv.dto.UserCsvDTO;
import com.example.erv.dto.projection.UserMinimal;
import com.example.erv.dto.report.absence.AbsenceReportRequest;
import com.example.erv.dto.report.daily.DailyReportRequest;
import com.example.erv.dto.report.workinghours.WorkingHoursReportRequest;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Card;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;
import com.example.erv.model.enums.UserType;
import com.example.erv.repository.user.UserDailyReportDTO;
import com.example.erv.repository.user.UserRepository;
import com.example.erv.service.card.CardAttribute;
import com.example.erv.service.card.CardService;
import com.example.erv.service.departmentunit.DepartmentUnitService;
import com.example.erv.service.role.RoleService;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.AppUtil;
import com.example.erv.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService extends UserServiceBasic implements UserDetailsService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DepartmentUnitService departmentUnitService;
    @Autowired
    private CardService cardService;

    public UserService(UserRepository repository) {
        super(repository);
    }

    @Override
    public User insert(User object) throws ErvException {
        if (isAdminUser(object)) {
            object.setPassword(AppUtil.encodePassword(object.getPassword()));
            if (object.getRole() == null) object.setRole(roleService.findByName("SuperUser"));
        }
        if (object.getUserType() == UserType.EMPLOYEE) {
            object.setRole(null);
            object.setUsername(null);
            object.setPassword(null);
        }
        object.setSoftDeleted(false);
        return super.insert(object);
    }

    @Override
    public User update(User object) throws ErvException {
        Optional<User> user = findById(object.getId());
        if (user.isEmpty()) {
            throw new ErvException("User does not exist.");
        }
        if (object.getUserType() == UserType.EMPLOYEE) {
            object.setRole(null);
            object.setUsername(null);
            object.setPassword(null);
        }

        if (isUserStillAdmin(object, user.get())) {
            object.setPassword(user.get().getPassword());
        }

        if (isUserFromEmployeeToAdmin(object, user.get())) {
            object.setPassword(AppUtil.encodePassword(object.getPassword()));
        }

        object.setSoftDeleted(false);
        return super.update(object);
    }

    private static boolean isUserStillAdmin(User object, User user) {
        return object.getUserType() == UserType.ADMIN && user.getUserType() == UserType.ADMIN;
    }

    private static boolean isUserFromEmployeeToAdmin(User object, User user) {
        return object.getUserType() == UserType.ADMIN && user.getUserType() == UserType.EMPLOYEE;
    }

    public User updatePassword(User object) throws ErvException {
        if (findAll().stream().noneMatch(user -> user.getId().equals(object.getId()))) {
            throw new AccessDeniedException("Access denied! You do not have permission for this action.");
        }
        Optional<User> optionalUser = findById(object.getId());
        if (optionalUser.isEmpty()) {
            throw new ErvException("User does not exist.");
        }
        User user = optionalUser.get();
        user.setPassword(AppUtil.encodePassword(object.getPassword()));
        return super.update(user);
    }

    public User changePassword(PasswordChangeDTO passwordChangeDTO) throws Exception {
        User user = findById(passwordChangeDTO.getId()).orElseThrow();
        if (!AppUtil.PASSWORD_ENCODER.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword()))
            throw new PasswordMismatchException("Current password don't match.");

        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword()))
            throw new PasswordMismatchException("New password and confirm password don't match.");

        user.setPassword(AppUtil.encodePassword(passwordChangeDTO.getNewPassword()));
        return super.update(user);
    }

    @Override
    public User save(User user) {
        user.setSoftDeleted(false);
        return super.save(user);
    }

    @Override
    public List<User> saveAll(List<User> users) {
        users.forEach(e -> e.setSoftDeleted(false));
        return super.saveAll(users);
    }

    /**
     * Imports a list of users and creates/connects Card entities by RFID.
     */
    public void importUsers(List<UserCsvDTO> users) throws ErvException {
        for (UserCsvDTO userRaw : users) {
            User user = userRaw.toEntity(departmentUnitService);
            user = insert(user);
            if (StringUtils.isBlank(userRaw.getRfid()))
                continue;
            assignCard(userRaw, user);
        }
    }

    private void assignCard(UserCsvDTO userRaw, User user) throws ErvException {
        try {
            Card card = cardService.findByRfid(userRaw.getRfid());
            if (card.getUser() != null) {
                logger.error("Card assignment skipped because this rfid [{}] is already in use.", userRaw.getRfid());
                return;
            }
            card.setUser(user);
            card.setActive(true);
            cardService.update(card);
        } catch (NoSuchElementException e) {
            cardService.createCardForUser(user, userRaw.getRfid());
        }
    }

    private boolean isAdminUser(User object) {
        return object.getUserType() == UserType.ADMIN && object.getPassword() != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> myUser = repository.findByUsername(username);
        if (myUser.isEmpty()) {
            throw new UsernameNotFoundException("User name " + username + " not found");
        }
        return new org.springframework.security.core.userdetails.User(myUser.get().getUsername(), myUser.get().getPassword(), myUser.get().getRole().getPermissionList());
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    public Page<User> findAllForDailyReport(DailyReportRequest request, Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        List<DepartmentUnit> departmentUnit = request.getDepartmentUnit();
        if (departmentUnit == null || departmentUnit.isEmpty()) {
            departmentUnit = accessible;
        }
        ZonedDateTime from = request.getDateTime().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId()));
        return repository.findAll(UserQueries.findAllForDailyReport(request.getRecordNumber(), departmentUnit, request.getUser(), accessible, from), pageable);
    }

    public Page<User> findAllForAbsenceReport(AbsenceReportRequest request, Pageable pageable) {
        List<DepartmentUnit> departmentUnit = request.getDepartmentUnit();
        if (departmentUnit == null || departmentUnit.isEmpty()) {
            User user = securityUtils.getCurrentUser();
            departmentUnit = departmentUnitService.getAllAccessible(user);
        }
        ZonedDateTime from = request.getDate().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId()));
        return findAllNotMandatoryDepartmentUnitAndUser(departmentUnit, request.getUser(), pageable, from);
    }

    public Page<User> findAllWorkingHoursReport(WorkingHoursReportRequest request, Pageable pageable) {
        List<DepartmentUnit> departmentUnit = request.getDepartmentUnit();
        if (departmentUnit == null || departmentUnit.isEmpty()) {
            User user = securityUtils.getCurrentUser();
            departmentUnit = departmentUnitService.getAllAccessible(user);
        }
        return findAllNotMandatoryDepartmentUnitAndUser(departmentUnit, request.getUser(), pageable, request.getDateFrom());
    }

    private Page<User> findAllNotMandatoryDepartmentUnitAndUser(List<DepartmentUnit> departmentUnit, User user, Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(currentUser);
        if (departmentUnit == null || departmentUnit.isEmpty()) {
            departmentUnit = accessible;
        }
        return repository.findAll(UserQueries.findAllNonMandatoryDepartmentUnitAndUser(departmentUnit, user, accessible), pageable);
    }
    
    private Page<User> findAllNotMandatoryDepartmentUnitAndUser(List<DepartmentUnit> departmentUnit, User user, Pageable pageable, ZonedDateTime reportStartDate) {
    	User currentUser = securityUtils.getCurrentUser();
    	List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(currentUser);
    	if (departmentUnit == null || departmentUnit.isEmpty()) {
    		departmentUnit = accessible;
    	}
    	return repository.findAll(UserQueries.findAllNonMandatoryDepartmentUnitAndUser(departmentUnit, user, accessible, reportStartDate), pageable);
    }

    public List<UserDailyReportDTO> findReportData(ZonedDateTime startDate, ZonedDateTime endDate, List<User> users, String recordNumber, List<DepartmentUnit> departmentUnit) {
        User user = securityUtils.getCurrentUser();
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        if (departmentUnit == null || departmentUnit.isEmpty()) {
            departmentUnit = accessible;
        }
        return repository.findReportData(startDate.toInstant(), endDate.toInstant(), users.stream().map(User::getId).toList(), recordNumber, departmentUnit, accessible);
    }

    public List<UserDailyReportDTO> findDashboardData(List<User> users, ZonedDateTime endDate, ZonedDateTime startDate) {
        User user = securityUtils.getCurrentUser();
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        return repository.findUserDashboardData(startDate.toInstant(), endDate.toInstant(), users.stream().map(User::getId).toList(), accessible);
    }

    @Override
    public List<User> findAll() {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
            SpecificationBasic<User> activeUsersSpec = SpecificationBasic.where(UserAttribute.SOFT_DELETED, false);
            return super.findAll(activeUsersSpec.getSpecification());
        }
        List<DepartmentUnit> cachedDepartmentUnits = departmentUnitService.getAllAccessible(user);
        SpecificationBasic<User> spec = SpecificationBasic.where(UserAttribute.DEPARTMENT_UNIT, cachedDepartmentUnits);
        return super.findAll(spec.getSpecification());
    }

    @Override
    public List<UserMinimal> findAllMinimal() {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
            return super.findAllMinimal();
        }
        List<DepartmentUnit> cachedDepartmentUnits = departmentUnitService.getAllAccessible(user);
        SpecificationBasic<User> spec = SpecificationBasic.where(UserAttribute.DEPARTMENT_UNIT, cachedDepartmentUnits);

        return repository.findBy(spec.getSpecification(), query -> query.as(UserMinimal.class).all());
    }

    @Override
    public Page<User> findAll(Specification<User> specification, Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
            return super.findAll(SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, false).and(specification).getSpecification(), pageable);
        }
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        SpecificationBasic<User> spec = SpecificationBasic.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(specification).and(UserAttribute.SOFT_DELETED, false);
        return super.findAll(spec.getSpecification(), pageable);
    }
    
    public Page<User> findAll(Specification<User> specification, Pageable pageable, boolean withSoftDelete) {
    	User user = securityUtils.getCurrentUser();
    	SpecificationBasic<User> spec;
    	if(withSoftDelete) {
    		if (SecurityUtils.isSuperUser(user)) {
    			return super.findAll(SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, false).and(specification).getSpecification(), pageable);
    		}
    		List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
    		spec = SpecificationBasic.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(specification).and(UserAttribute.SOFT_DELETED, false);
    	}else {
    		if (SecurityUtils.isSuperUser(user)) {
    			return super.findAll(SpecificationBasic.<User>get().and(specification).getSpecification(), pageable);
    		}
    		List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
    		spec = SpecificationBasic.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(specification);
    	}
    	return super.findAll(spec.getSpecification(), pageable);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
            return super.findAll(SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, false).getSpecification(), pageable);
        }
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        SpecificationBasic<User> spec = SpecificationBasic.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(UserAttribute.SOFT_DELETED, false);
        return super.findAll(spec.getSpecification(), pageable);
    }

    @Override
    public List<User> findAll(Specification<User> specification) {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
            return super.findAll(SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, false).and(specification).getSpecification());
        }
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        SpecificationBasic<User> spec = SpecificationBasic.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(specification).and(UserAttribute.SOFT_DELETED, false);
        return super.findAll(spec.getSpecification());
    }
    
    public List<User> findAll(Specification<User> specification, boolean withSoftDelete) {
    	User user = securityUtils.getCurrentUser();
    	SpecificationBasic<User> spec;
    	if(withSoftDelete) {
	    	if (SecurityUtils.isSuperUser(user)) {
	    		return super.findAll(SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, false).and(specification).getSpecification());
	    	}
	    	List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
	    	spec = SpecificationBasic.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(specification).and(UserAttribute.SOFT_DELETED, false);
		}else {
			if (SecurityUtils.isSuperUser(user)) {
				return super.findAll(SpecificationBasic.<User>get().and(specification).getSpecification());
			}
			List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
			spec = SpecificationBasic.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(specification);
		}
    	return super.findAll(spec.getSpecification());
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
            return Optional.ofNullable(super.findAll(SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, false).and(UserAttribute.ID, id).getSpecification()).get(0));
        }
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        SpecificationBasic<User> spec = SpecificationBasic.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(UserAttribute.ID, id).and(UserAttribute.SOFT_DELETED, false);
        return Optional.ofNullable(super.findAll(spec.getSpecification()).get(0));
    }

    @Override
    public void deleteById(Long id) throws ErvException {
        User user = findById(id).orElseThrow(() -> new ErvException("User not found"));
        user.setSoftDeleted(true);
        user.setRole(null);
        user.setJmbg(null);
        repository.save(user);
        disconnectUserCards(user);
    }

    private void disconnectUserCards(User user) throws ErvException {
        List<Card> cards = cardService.findAll(SpecificationBasic.<Card>where(CardAttribute.USER, user).getSpecification());
        if (!cards.isEmpty()) {
            for (Card card : cards) {
                card.setUser(null);
                card.setActive(false);
                cardService.update(card);
            }
        }
    }
}
 