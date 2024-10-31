package com.example.erv.controller;

import com.example.erv.exceptions.ErvException;
import com.example.erv.service.GenericService;
import com.example.erv.specification.SearchDTO;
import com.example.erv.specification.SpecificationBasic;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Getter
public class GenericController<T> {
    private final GenericService<T> service;

    public GenericController(GenericService<T> service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping(value = "/minimal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllMinimal() {
    	return ResponseEntity.ok(service.findAllMinimal());
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchPost(@RequestBody SearchDTO request) throws ErvException {
        SpecificationBasic<T> specification = request.buildSpecification(service.getEntityClass());
        return ResponseEntity.ok(service.findAll(specification.getSpecification()));
    }

    @PostMapping(value = "/searchPageable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchPageablePost(@RequestBody SearchDTO request) throws ErvException {
        SpecificationBasic<T> specification = request.buildSpecification(service.getEntityClass());
        return ResponseEntity.ok(service.findAll(specification.getSpecification(), request.createPageable()));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) throws ErvException {
        Optional<T> optional = service.findById(id);
        if (optional.isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new EntityNotFoundException("id:" + id.toString());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        Optional<T> optional = service.findById(id);
        if (optional.isPresent()) {
            return ResponseEntity.ok(optional.get());
        }
        throw new EntityNotFoundException("id:" + id.toString());
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@Valid @RequestBody T object) throws ErvException {
        return ResponseEntity.ok(service.update(object));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insert(@Valid @RequestBody T object) throws ErvException {
        return ResponseEntity.ok(service.insert(object));
    }
}
