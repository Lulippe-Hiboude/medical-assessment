package com.medical.assessment.patientms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.persistence.repository.PatientRepository;
import com.medical.assessment.patientms.security.jwt.JwtService;
import com.medical.assessment.patientms.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("should get patient by id")
    void shouldGetPatientById() throws Exception {
        //given
        final String token = jwtService.generateToken("doctor", "DOCTOR");
        final Long id = 1L;
        final PatientDto patientDto = patientService.getPatientById(id);

        //when
        final MvcResult result = mockMvc.perform(get("/patient/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final String responseBody = result.getResponse().getContentAsString();
        final String expectedResponseBody = objectMapper.writeValueAsString(patientDto);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("should get all patients")
    void shouldGetAllPatients() throws Exception {
        //given
        final String token = jwtService.generateToken("organizer", "ORGANIZER");

        //when
        final MvcResult result = mockMvc.perform(get("/patient")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final String responseBody = result.getResponse().getContentAsString();
        final String expectedResponseBody = objectMapper.writeValueAsString(patientService.getAllPatients());
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isEqualTo(expectedResponseBody);
    }
}
