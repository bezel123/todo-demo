package com.example.tododemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.tododemo.dao.TodoRepository;
import com.example.tododemo.model.Todo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * PUT /todo
     * 
     * updates todo object
     * 
     * @param t new todo object
     * @return 
     */
    @RequestMapping(value = "/todo",method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTodo(@RequestBody Todo t){
        try {
            if(!checkSize(t.getTitle(), 1, 30)){
                return new ResponseEntity<String>("Invalid modified todo.",HttpStatus.BAD_REQUEST);
            }else if(!checkSize(t.getDescription(), 0, 500)){
                return new ResponseEntity<String>("Invalid modified todo.",HttpStatus.BAD_REQUEST;
            }
            todoRepo.deleteById(t.getId());
            todoRepo.save(t);
            return new ResponseEntity<String>("Todo updated.",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Todo not found.",HttpStatus.NOT_FOUND);
        }
    }

    /**
     * checks wether string is in bounds
     * 
     * @param s string to check
     * @param lower bound
     * @param upper bound
     * @return true if correct
     */
    private boolean checkSize(String s,int lower,int upper){
        return s.length()>=lower&&s.length()<=upper;
    }
}