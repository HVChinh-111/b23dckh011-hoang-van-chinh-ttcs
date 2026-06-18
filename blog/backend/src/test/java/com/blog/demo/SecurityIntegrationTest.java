package com.blog.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

/**
 * End-to-end checks of the security rules and the auth -> write happy path,
 * running against an in-memory H2 database.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void publicListingsAreAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/posts")).andExpect(status().isOk());
        mockMvc.perform(get("/api/topics")).andExpect(status().isOk());
        mockMvc.perform(get("/api/series")).andExpect(status().isOk());
        mockMvc.perform(get("/api/profile")).andExpect(status().isOk());
    }

    @Test
    void adminEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"T","slug":"t","contentMarkdown":"c","status":"DRAFT"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginIssuesTokenAndAllowsAdminWrite() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@blog.local","password":"Admin@12345"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andReturn();

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(),
                "$.data.accessToken");
        assertThat(token).isNotBlank();

        mockMvc.perform(post("/api/topics")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Java","slug":"java","description":"Java topic"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("java"));
    }

    @Test
    void invalidLoginReturnsGenericError() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@blog.local","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }
}
