package com.medical.assessment.patientms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.patientms.patient.model.*;
import com.medical.assessment.patientms.persistence.entity.Patient;
import com.medical.assessment.patientms.persistence.repository.PatientRepository;
import com.medical.assessment.patientms.security.jwt.JwtService;
import com.medical.assessment.patientms.domain.patient.service.PatientServiceImpl;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private PatientServiceImpl patientServiceImpl;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("should get patient by id")
    void shouldGetPatientById() throws Exception {
        //given
        final String token = jwtService.generateToken("doctor", "DOCTOR");
        final Long id = 1L;
        final PatientDto patientDto = patientServiceImpl.getPatientById(id);

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
        final String expectedResponseBody = objectMapper.writeValueAsString(patientServiceImpl.getAllPatients());
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("should create patient")
    void shouldCreatePatient() throws Exception {
        //given
        final String token = jwtService.generateToken("organizer", "ORGANIZER");
        final String firstName = "Jane";
        final String lastName = "Doe";
        final LocalDate birthDate = LocalDate.of(1995, 1, 1);
        final Gender gender = Gender.F;
        final String address = "123 Main St";
        final String phoneNumber = "012-345-6789";

        final PatientCreateDto patientCreateDto = getPatientCreateDto(firstName, lastName, birthDate, gender, address, phoneNumber);

        final List<Patient> patientsBefore = patientRepository.findAll();
        assertThat(patientsBefore).isNotNull();
        assertThat(patientsBefore.size()).isEqualTo(2);

        //when
        final MvcResult result = mockMvc.perform(post("/patient")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientCreateDto))
                ).andExpect(status().isOk())
                .andReturn();

        //then
        final String responseBody = result.getResponse().getContentAsString();
        final PatientDto patientDto = objectMapper.readValue(responseBody, PatientDto.class);
        assertThat(patientDto).isNotNull();
        assertThat(patientDto.getFirstName()).isEqualTo(patientCreateDto.getFirstName());
        assertThat(patientDto.getLastName()).isEqualTo(patientCreateDto.getLastName());
        assertThat(patientDto.getBirthDate()).isEqualTo(patientCreateDto.getBirthDate());
        assertThat(patientDto.getGender().getValue()).isEqualTo(patientCreateDto.getGender().getValue());
        assertThat(patientDto.getAddress()).isEqualTo(patientCreateDto.getAddress());
        assertThat(patientDto.getPhoneNumber()).isEqualTo(patientCreateDto.getPhoneNumber());

        final List<Patient> patients = patientRepository.findAll();
        assertThat(patients).isNotNull();
        assertThat(patients.size()).isEqualTo(3);

        final Patient patient = patients.get(2);
        assertThat(patient.getFirstName()).isEqualTo(patientCreateDto.getFirstName());
        assertThat(patient.getLastName()).isEqualTo(patientCreateDto.getLastName());
        assertThat(patient.getBirthDate()).isEqualTo(patientCreateDto.getBirthDate());
        assertThat(patient.getGender().getValue()).isEqualTo(patientCreateDto.getGender().getValue());
        assertThat(patient.getAddress()).isEqualTo(patientCreateDto.getAddress());
        assertThat(patient.getPhoneNumber()).isEqualTo(patientCreateDto.getPhoneNumber());
    }

    @Test
    @DisplayName("should update patient")
    void shouldUpdatePatient() throws Exception {
        //given
        final String token = jwtService.generateToken("organizer", "ORGANIZER");

        final Long id = 1L;
        final String lastName = "Smith";
        final String address = "5 High street";
        final String phoneNumber = "123-456-7777";

        final PatientUpdateDto updateDto = new PatientUpdateDto();
        updateDto.setFirstName(null);
        updateDto.setLastName(lastName);
        updateDto.setGender(null);
        updateDto.setBirthDate(null);
        updateDto.setAddress(address);
        updateDto.setPhoneNumber(phoneNumber);

        //when
        final MvcResult result = mockMvc.perform(patch("/patient/{id}", id)
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                ).andExpect(status().isOk())
                .andReturn();

        //then
        final String responseBody = result.getResponse().getContentAsString();
        final PatientDto patientDto = objectMapper.readValue(responseBody, PatientDto.class);
        assertThat(patientDto)
                .usingRecursiveComparison()
                .comparingOnlyFields("lastName", "address", "phoneNumber")
                .isEqualTo(updateDto);

        final Patient patient = patientRepository.findByPatientId(id).get();
        assertThat(patient)
                .usingRecursiveComparison()
                .ignoringFields("patientId", "id")
                .isEqualTo(patientDto);
    }

    @Test
    @DisplayName("should get patient risk profile")
    void shouldGetPatientRiskProfile() throws Exception {
        //given
        final String token = jwtService.generateToken("doctor", "DOCTOR");
        final Long id = 1L;
        final PatientRiskProfile patientRiskProfile= patientServiceImpl.getPatientRiskProfileById(id);

        //when
        final MvcResult result = mockMvc.perform(get("/patient/{id}/risk-profile", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final String responseBody = result.getResponse().getContentAsString();
        final String expectedResponseBody = objectMapper.writeValueAsString(patientRiskProfile);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isEqualTo(expectedResponseBody);

    }

    @Test
    @DisplayName("should return true if patient exists")
    void shouldReturnTrueIfPatientExists() throws Exception {
        //given
        final String token = jwtService.generateToken("doctor", "DOCTOR");
        final Long id = 1L;

        //when
        final MvcResult result = mockMvc.perform(get("/patient/{id}/exists", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        //then
        final String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("true");
    }

    @Test
    @DisplayName("should return false if patient does not exist")
    void shouldReturnFalseIfPatientDoesNotExist() throws Exception {
        //given
        final String token = jwtService.generateToken("doctor", "DOCTOR");
        final Long id = 18L;

        //when
        final MvcResult result = mockMvc.perform(get("/patient/{id}/exists", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        //then
        final String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("false");
    }

    private static PatientCreateDto getPatientCreateDto(final String firstName,
                                                        final String lastName,
                                                        final LocalDate birthDate,
                                                        final Gender gender,
                                                        final String address,
                                                        final String phoneNumber) {

        final PatientCreateDto patientCreateDto = new PatientCreateDto();
        patientCreateDto.setFirstName(firstName);
        patientCreateDto.setLastName(lastName);
        patientCreateDto.setBirthDate(birthDate);
        patientCreateDto.setGender(gender);
        patientCreateDto.setAddress(address);
        patientCreateDto.setPhoneNumber(phoneNumber);
        return patientCreateDto;
    }
}
