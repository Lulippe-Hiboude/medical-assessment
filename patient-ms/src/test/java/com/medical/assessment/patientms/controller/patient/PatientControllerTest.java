package com.medical.assessment.patientms.controller.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.patientms.exception.PatientAlreadyExistsException;
import com.medical.assessment.patientms.exception.PatientNotFoundException;
import com.medical.assessment.patientms.patient.model.Gender;
import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.patient.model.PatientUpdateDto;
import com.medical.assessment.patientms.security.jwt.JwtService;
import com.medical.assessment.patientms.service.PatientServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientServiceImpl patientServiceImpl;

    @MockitoBean
    private JwtService jwtService;

    // =========================
    // GET ALL PATIENTS
    // =========================
    @Nested
    @DisplayName("getAllPatients")
    class getAllPatients {
        @Test
        @DisplayName("should get all patients")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldGetAllPatients() throws Exception {
            //given
            final PatientDto patientDto1 = getPatientDto();
            final PatientDto patientDto2 = getPatientDto();
            given(patientServiceImpl.getAllPatients()).willReturn(List.of(patientDto1, patientDto2));

            //when
            final MvcResult result = mockMvc.perform(get("/patient"))
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            final String responseBody = result.getResponse().getContentAsString();
            final String expectedResponseBody = objectMapper.writeValueAsString(List.of(patientDto1, patientDto2));
            assertThat(responseBody).isEqualTo(expectedResponseBody);
        }

        @Test
        @DisplayName("should return empty list when no patients found")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnEmptyListWhenNoPatientsFound() throws Exception {
            //given
            given(patientServiceImpl.getAllPatients()).willReturn(List.of());

            //when
            final MvcResult result = mockMvc.perform(get("/patient"))
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            final String responseBody = result.getResponse().getContentAsString();
            final String expectedResponseBody = objectMapper.writeValueAsString(List.of());
            assertThat(responseBody).isEqualTo(expectedResponseBody);
        }
    }

    // =========================
    // GET PATIENT BY ID
    // =========================
    @Nested
    @DisplayName("getPatientById")
    class getPatientById {
        @Test
        @DisplayName("should get patient by id")
        @WithMockUser(username = "doctor", roles = "DOCTOR")
        void shouldGetPatientById() throws Exception {
            //given
            final Long id = 1L;
            final PatientDto patientDto = getPatientDto();
            given(patientServiceImpl.getPatientById(id)).willReturn(patientDto);

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
            given(patientServiceImpl.getPatientById(id)).willThrow(new PatientNotFoundException("Patient not found with id: " + id));

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
    }

    // =========================
    // CREATE PATIENT
    // =========================
    @Nested
    @DisplayName("createPatient")
    class createPatient {
        @Test
        @DisplayName("should create patient")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldCreatePatient() throws Exception {
            //given
            final PatientCreateDto patientCreateDto = getPatientCreateDto();
            final PatientDto patientDto = getPatientDto();
            given(patientServiceImpl.createPatient(patientCreateDto)).willReturn(patientDto);

            //when
            final MvcResult result = mockMvc.perform(post("/patient")
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(patientCreateDto)))
                    .andExpect(status().isOk())
                    .andReturn();


            //then
            final String responseBody = result.getResponse().getContentAsString();
            final String expectedResponseBody = objectMapper.writeValueAsString(patientDto);
            assertThat(responseBody).isEqualTo(expectedResponseBody);

        }

        @Test
        @DisplayName("should return BAD_REQUEST when invalid patient data")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnBadRequestWhenInvalidPatientData() throws Exception {
            //given
            final PatientCreateDto invalidPatientCreateDto = new PatientCreateDto();
            invalidPatientCreateDto.setFirstName(null);
            invalidPatientCreateDto.setLastName(null);
            invalidPatientCreateDto.setBirthDate(null);

            //when
            MvcResult result = mockMvc.perform(post("/patient")
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(invalidPatientCreateDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //then
            final String responseBody = result.getResponse().getContentAsString();
            assertThat(responseBody)
                    .contains("firstName")
                    .contains("First name is required");

            assertThat(responseBody)
                    .contains("lastName")
                    .contains("Last name is required");

            assertThat(responseBody)
                    .contains("birthDate")
                    .contains("Birthdate is mandatory");

            assertThat(responseBody)
                    .contains("gender")
                    .contains("Gender is mandatory");
        }

        @Test
        @DisplayName("should return BAD_REQUEST when future birthdate")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnBadRequestWhenFutureBirthdate() throws Exception {
            //given
            final PatientCreateDto invalidPatientCreateDto = getPatientCreateDto();
            invalidPatientCreateDto.setBirthDate(LocalDate.now().plusDays(1));

            //when
            MvcResult result = mockMvc.perform(post("/patient")
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(invalidPatientCreateDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //then
            final String responseBody = result.getResponse().getContentAsString();
            assertThat(responseBody)
                    .contains("birthDate")
                    .contains("Birthdate must be a past date");
        }

        @Test
        @DisplayName("should return CONFLICT when patient already exists")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnConflictWhenPatientAlreadyExists() throws Exception {
            //given
            final PatientCreateDto patientCreateDto = getPatientCreateDto();
            given(patientServiceImpl.createPatient(patientCreateDto)).willThrow(new PatientAlreadyExistsException("Patient already exists"));

            //when
            MvcResult result = mockMvc.perform(post("/patient")
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(patientCreateDto)))
                    .andExpect(status().isConflict())
                    .andReturn();
        }
    }

    //TODO CREATE TEST CASES
    // =========================
    // UPDATE PATIENT
    // =========================
    @Nested
    @DisplayName("updatePatient")
    class updatePatient {

        @Test
        @DisplayName("should update patient")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldUpdatePatient() throws Exception {
            //given
            final long id = 1L;
            final String lastName = "Smith";
            final String address = "1 High street";
            final String phoneNumber = "123-456-7777";

            final PatientUpdateDto updateDto = new PatientUpdateDto();
            updateDto.setFirstName(null);
            updateDto.setLastName(lastName);
            updateDto.setGender(null);
            updateDto.setBirthDate(null);
            updateDto.setAddress(address);
            updateDto.setPhoneNumber(phoneNumber);

            final PatientDto patientDto = getPatientUpdate(lastName, address, phoneNumber);

            given(patientServiceImpl.updatePatient(id, updateDto)).willReturn(patientDto);

            //when & then
            MvcResult result = mockMvc.perform(patch("/patient/{id}", id)
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andReturn();
            final String responseBody = result.getResponse().getContentAsString();
            final String expectedResponseBody = objectMapper.writeValueAsString(patientDto);
            assertThat(responseBody).isEqualTo(expectedResponseBody);
        }

        @Test
        @DisplayName("should return BAD_REQUEST when invalid patient update data")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnBadRequestWhenInvalidPatientUpdateData() throws Exception {
            //given
            final long id = 1L;

            //when & then
            MvcResult result = mockMvc.perform(patch("/patient/{id}", id)
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(null)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        @DisplayName("should return BAD_REQUEST when invalid Id")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnBadRequestWhenInvalidId() throws Exception {
            //given
            final String invalidId = "abc";

            //when & then


        }

        @Test
        @DisplayName("should return BAD_REQUEST when Id equal zero")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnBadRequestWhenIdIsZero() throws Exception {
            //given
            final Long invalidId = 0L;
            final String lastName = "Smith";
            final String address = "1 High street";
            final String phoneNumber = "123-456-7777";

            final PatientUpdateDto updateDto = new PatientUpdateDto();
            updateDto.setFirstName(null);
            updateDto.setLastName(lastName);
            updateDto.setGender(null);
            updateDto.setBirthDate(null);
            updateDto.setAddress(address);
            updateDto.setPhoneNumber(phoneNumber);
            //when & then

            MvcResult result = mockMvc.perform(patch("/patient/{id}", invalidId)
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        @DisplayName("should return BAD_REQUEST when Id is negative")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnBadRequestWhenIdIsNegative() throws Exception {
            //given
            final Long invalidId = -1L;
            final String lastName = "Smith";
            final String address = "1 High street";
            final String phoneNumber = "123-456-7777";

            final PatientUpdateDto updateDto = new PatientUpdateDto();
            updateDto.setFirstName(null);
            updateDto.setLastName(lastName);
            updateDto.setGender(null);
            updateDto.setBirthDate(null);
            updateDto.setAddress(address);
            updateDto.setPhoneNumber(phoneNumber);
            //when & then

            MvcResult result = mockMvc.perform(patch("/patient/{id}", invalidId)
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

        }

        @Test
        @DisplayName("should return NOT_FOUND when patient not found")
        @WithMockUser(username = "organizer", roles = "ORGANIZER")
        void shouldReturnNotFoundWhenPatientNotFound() throws Exception {
            //given
            final Long id = 1L;
            final String lastName = "Smith";
            final String address = "1 High street";
            final String phoneNumber = "123-456-7777";

            final PatientUpdateDto updateDto = new PatientUpdateDto();
            updateDto.setFirstName(null);
            updateDto.setLastName(lastName);
            updateDto.setGender(null);
            updateDto.setBirthDate(null);
            updateDto.setAddress(address);
            updateDto.setPhoneNumber(phoneNumber);

            given(patientServiceImpl.updatePatient(id, updateDto)).willThrow(new PatientNotFoundException("Patient not found"));

            //when & then
            MvcResult result = mockMvc.perform(patch("/patient/{id}", id)
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNotFound())
                    .andReturn();

            final String responseBody = result.getResponse().getContentAsString();
            assertThat(responseBody)
                    .contains("Patient not found");
        }
    }

    private static PatientDto getPatientDto() {
        final LocalDate dob = LocalDate.of(1995, 1, 1);
        final Long id = 1L;
        final String firstName = "John";
        final String lastName = "Doe";
        final String address = "123 Main St";
        final String phoneNumber = "123-456-7890";
        final PatientDto.GenderEnum gender = PatientDto.GenderEnum.M;
        final PatientDto patientDto = new PatientDto();
        patientDto.setId(id);
        patientDto.setFirstName(firstName);
        patientDto.setLastName(lastName);
        patientDto.setBirthDate(dob);
        patientDto.setAddress(address);
        patientDto.setPhoneNumber(phoneNumber);
        patientDto.setGender(gender);

        return patientDto;
    }

    private PatientCreateDto getPatientCreateDto() {
        final String firstName = "John";
        final String lastName = "Doe";
        final LocalDate birthDate = LocalDate.of(1995, 1, 1);
        final String address = "123 Main St";
        final String phoneNumber = "123-456-7890";
        final Gender gender = Gender.M;

        final PatientCreateDto patientCreateDto = new PatientCreateDto();
        patientCreateDto.setFirstName(firstName);
        patientCreateDto.setLastName(lastName);
        patientCreateDto.setGender(gender);
        patientCreateDto.setBirthDate(birthDate);
        patientCreateDto.setAddress(address);
        patientCreateDto.setPhoneNumber(phoneNumber);

        return patientCreateDto;
    }

    private PatientDto getPatientUpdate(final String lastName, final String address, final String phoneNumber) {
        final PatientDto patientDto = getPatientDto();
        patientDto.setLastName(lastName);
        patientDto.setAddress(address);
        patientDto.setPhoneNumber(phoneNumber);
        return patientDto;
    }

}