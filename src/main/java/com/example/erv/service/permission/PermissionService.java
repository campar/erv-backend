package com.example.erv.service.permission;

import org.springframework.stereotype.Service;

import com.example.erv.repository.permission.PermissionRepository;

@Service
public class PermissionService extends PermissionServiceBasic {

	public PermissionService(PermissionRepository repository) {
		super(repository);
	}
}
