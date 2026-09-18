package com.practicum.posts.api;

import com.practicum.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        insertDefaultData();
    }

    @Test
    void getPosts_returnsFilteredPage() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .param("search", "spring #java")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].id").value(1))
                .andExpect(jsonPath("$.posts[0].title").value("Spring Boot"))
                .andExpect(jsonPath("$.posts[0].tags", hasSize(2)))
                .andExpect(jsonPath("$.posts[0].likesCount").value(5))
                .andExpect(jsonPath("$.posts[0].commentsCount").value(2))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    void getPostById_returnsFullText() throws Exception {
        String fullText = "Spring Boot ".repeat(20);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spring Boot"))
                .andExpect(jsonPath("$.text").value(fullText))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.likesCount").value(5))
                .andExpect(jsonPath("$.commentsCount").value(2));
    }

    @Test
    void createPost_persistsAndReturnsPost() throws Exception {
        String json = """
                {
                  "title": "New post",
                  "text": "New text",
                  "tags": ["new", "java"]
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("New post"))
                .andExpect(jsonPath("$.text").value("New text"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));

        mockMvc.perform(get("/api/posts")
                        .param("search", "")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(3)));
    }

    @Test
    void updatePost_updatesFieldsAndTags() throws Exception {
        String json = """
                {
                  "id": 1,
                  "title": "Updated title",
                  "text": "Updated text",
                  "tags": ["sql"]
                }
                """;

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.text").value("Updated text"))
                .andExpect(jsonPath("$.tags", hasSize(1)))
                .andExpect(jsonPath("$.tags[0]").value("sql"));
    }

    @Test
    void deletePost_returnsOkAndRemovesPost() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void likePost_incrementsLikes() throws Exception {
        mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(content().string("6"));

        mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @Test
    void uploadAndGetImage_works() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile(
                "image", "image.png", "image/png", pngStub);

        mockMvc.perform(multipart("/api/posts/1/image")
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(pngStub));
    }

    @Test
    void getPostById_notFound() throws Exception {
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }
}
