package com.example.erv.service.user;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import com.example.erv.dto.projection.UserMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Role;
import com.example.erv.model.User;
import com.example.erv.model.enums.UserType;
import com.example.erv.repository.user.UserRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class UserServiceBasic extends GenericService<User> {
	protected final UserRepository repository;

	public UserServiceBasic(UserRepository repository) {
		super(repository);
		this.repository = repository;
	}
    
    @Override
	public List<UserMinimal> findAllMinimal(){
		return repository.findAllProjected(UserMinimal.class);
	}

	@Override
	public User insert(User object) throws ErvException {
		if (object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
		return repository.save(object);
	}

	@Override
	public User update(User object) throws ErvException {
		if (object.getId() == null || repository.findById(object.getId()).isEmpty()) throw new EntityNotFoundException(String.valueOf(object.getId()));
		return repository.save(object);
	}

	@Override
	public Class<User> getEntityClass() {
		return User.class;
	}

	public User findByUsername(String username) {
		return repository.findByUsername(username).orElseThrow();
	}

	public List<User> findByPassword(String password) {
		return repository.findByPassword(password);
	}

	public User findByEmail(String email) {
		return repository.findByEmail(email).orElseThrow();
	}

	public List<User> findByCreationDate(ZonedDateTime creationDate) {
		return repository.findByCreationDate(creationDate.toInstant());
	}

	public List<User> findByModifyDate(ZonedDateTime modifyDate) {
		return repository.findByModifyDate(modifyDate.toInstant());
	}

	public List<User> findByFirstName(String firstName) {
		return repository.findByFirstName(firstName);
	}

	public List<User> findByLastName(String lastName) {
		return repository.findByLastName(lastName);
	}

	public List<User> findByParentName(String parentName) {
		return repository.findByParentName(parentName);
	}

	public User findByRecordNumber(String recordNumber) {
		return repository.findByRecordNumber(recordNumber).orElseThrow();
	}

	public List<User> findByBirthDate(Date birthDate) {
		return repository.findByBirthDate(birthDate);
	}

	public User findByJmbg(String jmbg) {
		return repository.findByJmbg(jmbg).orElseThrow();
	}

	public List<User> findByJobTitle(String jobTitle) {
		return repository.findByJobTitle(jobTitle);
	}

	public List<User> findByOfficeNumber(String officeNumber) {
		return repository.findByOfficeNumber(officeNumber);
	}

	public List<User> findByAvatar(String avatar) {
		return repository.findByAvatar(avatar);
	}

	public List<User> findByUserType(UserType userType) {
		return repository.findByUserType(userType);
	}

	public List<User> findByRole(Role role) {
		return repository.findByRole(role);
	}
}
