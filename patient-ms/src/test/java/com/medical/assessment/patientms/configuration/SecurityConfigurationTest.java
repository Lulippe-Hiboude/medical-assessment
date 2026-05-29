package com.medical.assessment.patientms.configuration;

import com.medical.assessment.patientms.security.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigurationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("should_allow_public_endpoint")
    void should_allow_public_endpoint() throws Exception {
        //when & then
        mockMvc.perform(get("/auth/token")
                        .param("username", "test")
                        .param("role", "DOCTOR"))
                .andExpect(status().isOk());
    }
}
