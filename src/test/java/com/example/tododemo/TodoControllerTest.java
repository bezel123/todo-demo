package com.example.tododemo;

import java.time.Instant;

import com.example.tododemo.controller.TodoController;
import com.example.tododemo.repository.TodoRepository;
import com.example.tododemo.model.Todo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    TodoRepository todoRepo;

    @InjectMocks
    private TodoController todoController;

    private void save(Todo todo) {
        when(todoRepo.save(todo)).thenReturn(todo);
    }

    @Test
    public void getTodoByIdTest() throws Exception {

        Todo todo = new Todo(1, "test", "test object", Instant.EPOCH, false);

        save(todo);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/todos/" + todo.getId())
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(todo.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(todo.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(todo.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate").value(todo.getDueDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.done").value(todo.isDone()));

        builder = MockMvcRequestBuilders.get("/todos/100").accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isNotFound());

    }

    @Test
    public void putTodoTest() throws Exception {

        Todo newTodo = new Todo(1, "new title", "test object", Instant.EPOCH, false);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/todos").content(newTodo.toJSON())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isNoContent());

        Todo todo2 = new Todo(3, "test", "test object", Instant.EPOCH, false);
        String newTodo2 = "{\"id\": 3,\"title\": \"\",\"description\": \"test object\",\"dueDate\": \"1970-01-01T00:00:00Z\",\"done\": false}";

        save(todo2);

        builder = MockMvcRequestBuilders.put("/todos").content(newTodo2).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andExpect(MockMvcResultMatchers
                        .jsonPath("$[0]").value("{'code': 'TITLE_NULL','message':'title must not be null'}"));

    }

    @Test
    public void deleteTodoTest() throws Exception {

        Todo todo = new Todo(1, "test", "test object", Instant.EPOCH, false);

        save(todo);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/todos/" + todo.getId())
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isOk());

        builder = MockMvcRequestBuilders.delete("/todos/100").accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isNotFound());

    }

    @Test
    public void postTodoTest() throws Exception {

        // all good
        Todo todo1 = new Todo(1, "test", "test object", Instant.EPOCH, false);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/todos").content(todo1.toJSON())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(todo1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(todo1.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(todo1.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate").value(todo1.getDueDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.done").value(todo1.isDone()));

        // title is null
        String todo2 = "{\"id\": 1,\"title\": \"\",\"description\": \"test1\",\"dueDate\": \"1970-01-01T00:00:00Z\",\"done\": false}";

        builder = MockMvcRequestBuilders.post("/todos").content(todo2).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isPreconditionFailed())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andExpect(MockMvcResultMatchers
                        .jsonPath("$[0]").value("{'code': 'TITLE_NULL','message':'title must not be null'}"));

        // description is above 500
        String todo3 = "{\"id\": 1,\"title\": \"title1\",\"description\": \"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.           Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.           Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.           Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis.           At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, At accusam aliquyam diam diam dolore dolores duo eirmod eos erat, et nonumy sed tempor et et invidunt justo labore Stet clita ea et gubergren, kasd magna no rebum. sanctus sea sed takimata ut vero voluptua. est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing\",\"dueDate\": \"1970-01-01T00:00:00Z\",\"done\": false}";

        builder = MockMvcRequestBuilders.post("/todos").content(todo3).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isPreconditionFailed())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]")
                        .value("{'code': 'DESCRIPTION_SIZE','message':'description size must be between 0 and 500'}"));

    }

    @Test
    public void getAllTodosTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/todos?state=all&limit=10&offset=0")
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder).andExpect(status().isPartialContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("test object"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dueDate").value("1970-01-01T00:00:00Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].done").value(false));

        builder = MockMvcRequestBuilders.get("/todos?state=unfinished&limit=10&offset=0")
                .accept(MediaType.APPLICATION_JSON);

    }
}