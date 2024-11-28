package com.example.erv.repository.user;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.Role;
import com.example.erv.model.User;
import com.example.erv.model.enums.UserType;
import com.example.erv.repository.GenericRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface UserRepositoryBasic extends GenericRepository<User> {

    Optional<User> findByUsername(String username);

    List<User> findByPassword(String password);

    Optional<User> findByEmail(String email);

    List<User> findByCreationDate(Instant creationDate);

    List<User> findByModifyDate(Instant modifyDate);

    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);

    List<User> findByParentName(String parentName);

    Optional<User> findByRecordNumber(String recordNumber);

    List<User> findByBirthDate(Date birthDate);

    Optional<User> findByJmbg(String jmbg);

    List<User> findByJobTitle(String jobTitle);

    List<User> findByOfficeNumber(String officeNumber);

    List<User> findByAvatar(String avatar);

    List<User> findByUserType(UserType userType);

    List<User> findByRole(Role role);

    List<User> findByDepartmentUnit(DepartmentUnit departmentUnit);

    Long countByDepartmentUnit(DepartmentUnit departmentUnit);

    @Override
	@Query("SELECT e FROM #{#entityName} e WHERE e.softDeleted = false")
	<P> List<P> findAllProjected(Class<P> projection);
}
