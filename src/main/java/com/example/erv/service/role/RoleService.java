package com.example.erv.service.role;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.erv.exceptions.ErvException;
import com.example.erv.exceptions.UserContainsRoleException;
import com.example.erv.model.Role;
import com.example.erv.model.User;
import com.example.erv.repository.role.RoleRepository;
import com.example.erv.repository.user.UserRepository;

@Service
public class RoleService extends RoleServiceBasic {
	private final UserRepository userRepository;

	public RoleService(RoleRepository repository, UserRepository userRepository) {
		super(repository);
		this.userRepository = userRepository;
	}

	@Override
	public void deleteById(Long id) throws ErvException {
		Role role = repository.findById(id).get();
		List<User> users = userRepository.findByRole(role);
		if (!users.isEmpty()) {
			throw new UserContainsRoleException("Some users contain this role. Please change the role on users then delete this role.");
//			for (User user : users) {
//				user.setRole(null);
//				userRepository.save(user);
//			}
		}
		super.deleteById(id);
	}
}
