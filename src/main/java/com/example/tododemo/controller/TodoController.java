package com.example.tododemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.tododemo.dao.TodoRepository;
import com.example.tododemo.model.Todo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {
    private TodoRepository todoRepo;

    public TodoController() {
    }

    /**
     * GET /todo/{id}
     * 
     * gives todo with specified id
     * 
     * @param id of the todo
     * @return todo object
     */
    @RequestMapping(value = "/todo/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getTodo(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<Todo>(todoRepo.findById(id).get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(id + " not found", HttpStatus.NOT_FOUND);
        }
    }

}