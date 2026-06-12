package com.medical.assessment.patientms.mapper;

import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.persistence.entity.Patient;
import com.medical.assessment.patientms.persistence.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class PatientMapperTest {

    @Test
    @DisplayName("should map patient to patient dto")
    void should_map_patient_to_patient_dto() {
        //given
        final Long id = 1L;
        final Patient patient = getPatient(id);

        //when
        final PatientDto patientDto = PatientMapper.INSTANCE.toPatientDto(patient);

        //then
        assertNotNull(patientDto);
        assertThat(patientDto.getFirstName()).isEqualTo(patient.getFirstName());
        assertThat(patientDto.getLastName()).isEqualTo(patient.getLastName());
        assertThat(patientDto.getGender().getValue()).isEqualTo(patient.getGender().getValue());
        assertThat(patientDto.getBirthDate()).isEqualTo(patient.getBirthDate());
        assertThat(patientDto.getAddress()).isEqualTo(patient.getAddress());
        assertThat(patientDto.getPhoneNumber()).isEqualTo(patient.getPhoneNumber());
    }

    @Test
    @DisplayName("should map patient create dto to patient")
    void should_map_patient_create_dto_to_patient() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final PatientCreateDto.GenderEnum gender = PatientCreateDto.GenderEnum.M;
        final String address = "123 Main St";
        final LocalDate birthDate = LocalDate.of(1995, 1, 1);
        final String phoneNumber = "1234567890";

        final PatientCreateDto patientCreateDto = new PatientCreateDto();
        patientCreateDto.setFirstName(firstName);
        patientCreateDto.setLastName(lastName);
        patientCreateDto.setGender(gender);
        patientCreateDto.setBirthDate(birthDate);
        patientCreateDto.setAddress(address);
        patientCreateDto.setPhoneNumber(phoneNumber);

        //when
        final Patient patient = PatientMapper.INSTANCE.toPatient(patientCreateDto);

        //then
        assertNotNull(patient);
        assertThat(patient.getFirstName()).isEqualTo(firstName);
        assertThat(patient.getLastName()).isEqualTo(lastName);
        assertThat(patient.getGender().getValue()).isEqualTo(gender.getValue());
        assertThat(patient.getBirthDate()).isEqualTo(birthDate);
        assertThat(patient.getAddress()).isEqualTo(address);
        assertThat(patient.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    private static Patient getPatient(final Long id) {
        final LocalDate dob = LocalDate.of(1995, 1, 1);
        final String firstName = "John";
        final String lastName = "Doe";
        final String address = "123 Main St";
        final String phoneNumber = "1234567890";
        final Gender gender = Gender.M;

        return Patient.builder()
                .patientId(id)
                .firstName(firstName)
                .lastName(lastName)
                .gender(gender)
                .birthDate(dob)
                .address(address)
                .phoneNumber(phoneNumber)
                .build();
    }
}