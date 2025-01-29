package com.example.triply.core.auth.resource;

import com.example.triply.TriplyApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthResource.class)
public class AuthResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginTest() throws Exception {
        mockMvc.perform(get("/api/${triply.api-version}/auth")).andExpect(status().isOk()).andExpect(content().string("Done"));
    }
}
