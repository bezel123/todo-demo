package com.example.tododemo.controller;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.format.datetime.standard.InstantFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.tododemo.dao.TodoRepository;
import com.example.tododemo.model.Todo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {
    private TodoRepository todoRepo;

    public TodoController() {
    }

    /**
     * GET /todos/{id}
     * 
     * gives todo with specified id
     * 
     * @param id of the todo
     * @return todo object
     */
    @RequestMapping(value = "/todos/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getTodo(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<Todo>(todoRepo.findById(id).get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /todos
     * 
     * updates todo object
     * 
     * @param t new todo object
     * @return confirmation
     */
    @RequestMapping(value = "/todos", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTodo(@RequestBody Todo t) {
        try {
            if (t.getTitle().equals("")) {
                return new ResponseEntity<String>("title must not be null", HttpStatus.PRECONDITION_FAILED);
            }
            if (t.getTitle().length() < 1 || t.getTitle().length() > 30) {
                return new ResponseEntity<String>("title size must be between 1 and 30",
                        HttpStatus.PRECONDITION_FAILED);
            }
            if (t.getDescription().length() < 0 || t.getDescription().length() > 500) {
                return new ResponseEntity<String>("description size must be between 0 and 500",
                        HttpStatus.PRECONDITION_FAILED);
            }
            if (t.getDueDate().equals("")) {
                return new ResponseEntity<String>("dueDate must not be null", HttpStatus.PRECONDITION_FAILED);
            }
            try {
                Instant i = new InstantFormatter().parse(t.getDueDate(), Locale.GERMAN);
                System.out.println(i.toString());
            } catch (Exception e) {
                return new ResponseEntity<String>("dueDate pattern must match \"yyyy-MM-ddTHH:mm:ssZ\"",
                        HttpStatus.PRECONDITION_FAILED);
            }
            todoRepo.deleteById(t.getId());
            todoRepo.save(t);
            return new ResponseEntity<String>("Todo updated.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Todo not found.", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE /todos/{id}
     * 
     * deletes todo object
     * 
     * @param t object to delete
     * @return confirmation
     */
    @RequestMapping(value = "/todos/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTodo(@RequestBody Todo t) {
        try {
            todoRepo.deleteById(t.getId());
            return new ResponseEntity<String>("Todo deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Todo not found.", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST /todos
     * 
     * creates new todo object
     * 
     * @param t new todo object
     * @return new todo object
     */
    @RequestMapping(value = "/todos", method = RequestMethod.POST)
    public ResponseEntity<?> createTodo(@RequestBody Todo t) {
        try {
            if (t.getTitle().equals("")) {
                return new ResponseEntity<String>("title must not be null", HttpStatus.BAD_REQUEST);
            }
            if (t.getTitle().length() < 1 || t.getTitle().length() > 30) {
                return new ResponseEntity<String>("title size must be between 1 and 30", HttpStatus.BAD_REQUEST);
            }
            if (t.getDescription().length() < 0 || t.getDescription().length() > 500) {
                return new ResponseEntity<String>("description size must be between 0 and 500", HttpStatus.BAD_REQUEST);
            }
            if (t.getDueDate().equals("")) {
                return new ResponseEntity<String>("dueDate must not be null", HttpStatus.BAD_REQUEST);
            }
            try {
                Instant i = new InstantFormatter().parse(t.getDueDate(), Locale.GERMAN);
                System.out.println(i.toString());
            } catch (Exception e) {
                return new ResponseEntity<String>("dueDate pattern must match \"yyyy-MM-ddTHH:mm:ssZ\"",
                        HttpStatus.BAD_REQUEST);
            }
            todoRepo.save(t);
            return new ResponseEntity<Todo>(t, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Invalid todo", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /todos
     * 
     * default gives all todo objects
     * 
     * @param state  Filters all or unfinished todos in the response: all or
     *               unfinished
     * @param limit  Maximal number of todos in the response: default=5
     * @param offset Offset for the todos in the response
     * @return List of specified objects
     */
    @RequestMapping(value = "/todos", method = RequestMethod.GET)
    public ResponseEntity<?> getAllTodos(@RequestParam String state, Integer limit, Integer offset) {
        try {
            ArrayList<String> errors = null;
            if ((errors = validInput(state, limit, offset)).isEmpty()) {
                if (limit == null) {
                    limit = 5;
                } else if (offset == null) {
                    offset = 0;
                }
                HttpStatus code = HttpStatus.OK;
                List<Todo> list = (List<Todo>) todoRepo.findAll();
                if (!state.equals("all")) {
                    list.removeIf(todo -> todo.isDone());
                    code = HttpStatus.PARTIAL_CONTENT;
                }
                PagedListHolder<Todo> l = new PagedListHolder<Todo>(list);
                l.setPageSize(limit);
                l.setPage(offset);
                return new ResponseEntity<>(l.getPageList(), code);
            } else {
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ArrayList<String> validInput(String state, Integer limit, Integer offset) {
        ArrayList<String> output = new ArrayList<>();
        if (!(state.equalsIgnoreCase("all") || state.equalsIgnoreCase("unfinished"))) {
            output.add(createErrMsg("STATE_INVALID", "state must be ALL or UNFINISHED"));
        } else if (limit < 0) {
            output.add(createErrMsg("LIMIT_MIN", "limit must be greater or equal to 0"));
        } else if (limit > 10) {
            output.add(createErrMsg("LIMIT_MAX", "limit must be less or equal to 10"));
        } else if (offset < 0) {
            output.add(createErrMsg("OFFSET_MIN", "offset must be greater or equal to 0"));
        } else if (offset > 100) {
            output.add(createErrMsg("OFFSET_MAX", "offset must be less or equal to 100"));
        }
        return output;
    }

    /**
     * 
     * creates a error message
     * 
     * @param code Error code
     * @param msg  Error message
     * @return Error in JSON
     */
    private String createErrMsg(String code, String msg) {
        return "{'code': '" + code + "'," + "'message':'" + msg + "'}";
    }
}