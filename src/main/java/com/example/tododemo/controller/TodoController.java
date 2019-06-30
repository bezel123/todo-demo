package com.example.tododemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.ArrayList;

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

    @Autowired
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
    public ResponseEntity<?> updateTodo(@RequestBody Todo t) {
        try {
            ArrayList<String> error = null;
            if ((error = validateInput(t.getTitle(), t.getDescription(), t.getDueDate())).isEmpty()) {
                todoRepo.deleteById(t.getId());
                todoRepo.save(t);
                return new ResponseEntity<String>("Todo updated.", HttpStatus.OK);
            }
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<String> deleteTodo(@PathVariable("id") int id) {
        try {
            todoRepo.deleteById(id);
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
            ArrayList<String> error = null;
            if ((error = validateInput(t.getTitle(), t.getDescription(), t.getDueDate())).isEmpty()) {
                todoRepo.save(t);
                return new ResponseEntity<Todo>(t, HttpStatus.OK);
            }
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<String>("Invalid todo", HttpStatus.BAD_REQUEST);
        }
    }

    private ArrayList<String> validateInput(String title, String desc, Instant date) {
        ArrayList<String> output = new ArrayList<>();
        if (title.equals("")) {
            output.add(createErrMsg("TITLE_NULL", "title must not be null"));
        }
        if (title.length() < 1 || title.length() > 30) {
            output.add(createErrMsg("TITLE_SIZE", "title size must be between 1 and 30"));
        }
        if (desc.length() < 0 || desc.length() > 500) {
            output.add(createErrMsg("DESCRIPTION_SIZE", "description size must be between 0 and 500"));
        }
        if (date == null) {
            output.add(createErrMsg("DUEDATE_NULL", "dueDate must not be null"));
        }
        return output;
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
    @RequestMapping(value = "/todos", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getAllTodos(@RequestParam String state, Integer limit, Integer offset) {
        try {
            ArrayList<String> error = null;
            // default values
            if (state.equals("")) {
                state = "unfinished";
            }
            if (limit == null) {
                limit = 5;
            }
            if (offset == null) {
                offset = 0;
            }
            if ((error = validQueryInput(state, limit, offset)).isEmpty()) {
                // get all todos
                ArrayList<Todo> list = toList(todoRepo.findAll());
                // check if empty
                if (list.isEmpty()) {
                    return new ResponseEntity<>("[]", HttpStatus.NO_CONTENT);
                }
                // filter finished
                filterFinished(list, state);
                // paginate
                PagedListHolder<Todo> pl = new PagedListHolder<>(list);
                pl.setPage(offset);
                pl.setPageSize(limit);
                // return list
                return new ResponseEntity<>(pl.getPageList(), HttpStatus.PARTIAL_CONTENT);
            }
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * converts Iterable to list
     * 
     * @param i iterable to convert
     * @return list from i
     */
    private ArrayList<Todo> toList(Iterable<Todo> i) {
        try {
            ArrayList<Todo> list = new ArrayList<>();
            i.forEach((Todo todo) -> {
                list.add(todo);
            });
            return list;
        } catch (Exception e) {
            System.err.println("[ERROR] parsing to List.");
            return new ArrayList<>();
        }
    }

    /**
     * 
     * filter all finshed todos
     * 
     * @param list  list to filter
     * @param state state of todo
     */
    private void filterFinished(ArrayList<Todo> list, String state) {
        try {
            if (state.equals("unfinished")) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isDone()) {
                        list.remove(i);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] filtering List.");
        }
    }

    /**
     * 
     * checks if the input is correct
     * 
     * @param state
     * @param limit
     * @param offset
     * @return array with errors
     */
    private ArrayList<String> validQueryInput(String state, int limit, int offset) {
        try {
            ArrayList<String> output = new ArrayList<>();
            if (!(state.equalsIgnoreCase("all") || state.equalsIgnoreCase("unfinished"))) {
                output.add(createErrMsg("STATE_INVALID", "state must be ALL or UNFINISHED"));
            }
            if (limit < 0) {
                output.add(createErrMsg("LIMIT_MIN", "limit must be greater or equal to 0"));
            }
            if (limit > 10) {
                output.add(createErrMsg("LIMIT_MAX", "limit must be less or equal to 10"));
            }
            if (offset < 0) {
                output.add(createErrMsg("OFFSET_MIN", "offset must be greater or equal to 0"));
            }
            if (offset > 100) {
                output.add(createErrMsg("OFFSET_MAX", "offset must be less or equal to 100"));
            }
            return output;
        } catch (Exception e) {
            return new ArrayList<>();
        }
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