package com.practicum.comments.api;

import com.practicum.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CommentControllerIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        insertDefaultData();
    }

    @Test
    void getComments_returnsList() throws Exception {
        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("First comment"))
                .andExpect(jsonPath("$[0].postId").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getComment_returnsComment() throws Exception {
        mockMvc.perform(get("/api/posts/1/comments/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.text").value("Second comment"))
                .andExpect(jsonPath("$.postId").value(1));
    }

    @Test
    void createComment_persists() throws Exception {
        String json = """
                {"text": "New comment", "postId": 1}
                """;

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value("New comment"))
                .andExpect(jsonPath("$.postId").value(1));

        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void updateComment_updatesText() throws Exception {
        String json = """
                {"id": 2, "text": "Updated comment", "postId": 1}
                """;

        mockMvc.perform(put("/api/posts/1/comments/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.text").value("Updated comment"))
                .andExpect(jsonPath("$.postId").value(1));
    }

    @Test
    void deleteComment_removesComment() throws Exception {
        mockMvc.perform(delete("/api/posts/1/comments/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/1/comments/2"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deleteComment_fromWrongPost_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/posts/2/comments/1"))
                .andExpect(status().isNotFound());
    }
}
