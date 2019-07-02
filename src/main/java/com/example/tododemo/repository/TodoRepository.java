package com.example.tododemo.repository;

import com.example.tododemo.model.Todo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TodoRepository extends CrudRepository<Todo, Integer> {
}