package com.medical.assessment.patientms.mapper;

import com.medical.assessment.patientms.domain.patient.mapper.PatientMapper;
import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.patient.model.PatientUpdateDto;
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
        final com.medical.assessment.patientms.patient.model.Gender gender = com.medical.assessment.patientms.patient.model.Gender.M;
        final String address = "123 Main St";
        final LocalDate birthDate = LocalDate.of(1995, 1, 1);
        final String phoneNumber = "123-456-7890";

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

    @Test
    @DisplayName("should eliminate empty spaces in first name and last name when mapping patient create dto to patient")
    void should_eliminate_empty_spaces_in_first_name_and_last_name_when_mapping_patient_create_dto_to_patient() {
        //given
        final String firstName = "  John  ";
        final String lastName = "  Doe  ";
        final String address = "123   Main   St";
        final String phoneNumber = "  123-456-7890  ";
        final LocalDate birthDate = LocalDate.of(1995, 1, 1);
        final com.medical.assessment.patientms.patient.model.Gender gender = com.medical.assessment.patientms.patient.model.Gender.M;
        final PatientCreateDto patientCreateDto = new PatientCreateDto();
        patientCreateDto.setFirstName(firstName);
        patientCreateDto.setLastName(lastName);
        patientCreateDto.setGender(gender);
        patientCreateDto.setBirthDate(birthDate);
        patientCreateDto.setAddress(address);
        patientCreateDto.setPhoneNumber(phoneNumber);

        //when
        final Patient patient = PatientMapper.INSTANCE.toPatient(patientCreateDto);
        assertNotNull(patient);
        final Patient expected = getPatient(1L);
        assertThat(patient)
                .usingRecursiveComparison()
                .ignoringFields("patientId")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("should update existing patient")
    void should_update_patient() {
        //given
        final PatientUpdateDto updateDto= new PatientUpdateDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setGender(com.medical.assessment.patientms.patient.model.Gender.F);
        updateDto.setBirthDate(LocalDate.of(1999, 1, 1));
        updateDto.setAddress("1 High street");
        updateDto.setPhoneNumber("123-456-7777");

        final Patient existingPatient = getPatient(1L);

        //when
        final Patient result = PatientMapper.INSTANCE.updatePatient(updateDto, existingPatient);

        //then
        assertThat(result).isNotNull();
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("patientId")
                .isEqualTo(updateDto);
    }

    @Test
    @DisplayName("should update and ignore null field ")
    void should_update_and_ignore_null_field() {
        //given
        final PatientUpdateDto updateDto= new PatientUpdateDto();
        updateDto.setFirstName(null);
        updateDto.setLastName("Smith");
        updateDto.setGender(null);
        updateDto.setBirthDate(null);
        updateDto.setAddress("1 High street");
        updateDto.setPhoneNumber("123-456-7777");

        final Patient existingPatient = getPatient(1L);

        //when
        final Patient result = PatientMapper.INSTANCE.updatePatient(updateDto, existingPatient);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(existingPatient.getFirstName());
        assertThat(result.getLastName()).isEqualTo(updateDto.getLastName());
        assertThat(result.getGender()).isEqualTo(existingPatient.getGender());
        assertThat(result.getBirthDate()).isEqualTo(existingPatient.getBirthDate());
        assertThat(result.getAddress()).isEqualTo(updateDto.getAddress());
        assertThat(result.getPhoneNumber()).isEqualTo(updateDto.getPhoneNumber());
    }

    @Test
    @DisplayName("should update patient with null value if address or phoneNumber empty")
    void should_update_patient_with_null_value(){
        //given
        final PatientUpdateDto updateDto= new PatientUpdateDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setGender(com.medical.assessment.patientms.patient.model.Gender.F);
        updateDto.setBirthDate(LocalDate.of(1999, 1, 1));
        updateDto.setAddress("");
        updateDto.setPhoneNumber("");

        final Patient existingPatient = getPatient(1L);

        //when
        final Patient result = PatientMapper.INSTANCE.updatePatient(updateDto, existingPatient);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isNull();
        assertThat(result.getPhoneNumber()).isNull();
    }

    private static Patient getPatient(final Long id) {
        final LocalDate dob = LocalDate.of(1995, 1, 1);
        final String firstName = "John";
        final String lastName = "Doe";
        final String address = "123 Main St";
        final String phoneNumber = "123-456-7890";
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