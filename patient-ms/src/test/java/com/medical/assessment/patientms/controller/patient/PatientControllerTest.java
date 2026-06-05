package com.medical.assessment.patientms.controller.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.patientms.exception.PatientNotFoundException;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.security.jwt.JwtService;
import com.medical.assessment.patientms.service.PatientService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("should get patient by id")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldGetPatientById() throws Exception {
        //given
        final Long id = 1L;
        final PatientDto patientDto = getPatientDto();
        given(patientService.getPatientById(id)).willReturn(patientDto);

        //when
        final MvcResult result = mockMvc.perform(get("/patient/{id}", id))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final String responseBody = result.getResponse().getContentAsString();
        final String expectedResponseBody = objectMapper.writeValueAsString(patientDto);
        assertThat(responseBody).isEqualTo(expectedResponseBody);

    }

    @Test
    @DisplayName("should return NOT_FOUND when patient not found")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnNotFoundWhenPatientNotFound() throws Exception {
        //given
        final Long id = 1L;
        given(patientService.getPatientById(id)).willThrow(new PatientNotFoundException("Patient not found with id: " + id));

        //when & then
        mockMvc.perform(get("/patient/{id}", id))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(username = "user", roles = "DOCTOR")
    void shouldReturnBadRequestWhenInvalidId() throws Exception {
        //given
        final String invalidId = "abc";

        //when & then
        mockMvc.perform(get("/patient/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "DOCTOR")
    void shouldReturnBadRequestWhenIdIsZero() throws Exception {
        //given
        final Long invalidId = 0L;

        //when & then
        mockMvc.perform(get("/patient/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "DOCTOR")
    void shouldReturnBadRequestWhenIdIsNegative() throws Exception {
        //given
        final Long invalidId = -1L;

        //when & then
        mockMvc.perform(get("/patient/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }

    private static PatientDto getPatientDto() {
        final LocalDate dob = LocalDate.of(1995, 1, 1);
        final String firstName = "John";
        final String lastName = "Doe";
        final String address = "123 Main St";
        final String phoneNumber = "1234567890";
        final PatientDto.GenderEnum gender = PatientDto.GenderEnum.M;
        final PatientDto patientDto = new PatientDto();
        patientDto.setFirstName(firstName);
        patientDto.setLastName(lastName);
        patientDto.setBirthDate(dob);
        patientDto.setAddress(address);
        patientDto.setPhoneNumber(phoneNumber);
        patientDto.setGender(gender);

        return patientDto;
    }

}