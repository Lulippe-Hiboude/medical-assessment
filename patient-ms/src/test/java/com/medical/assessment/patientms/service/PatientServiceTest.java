package com.medical.assessment.patientms.service;

import com.medical.assessment.patientms.exception.PatientNotFoundException;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.persistence.entity.Patient;
import com.medical.assessment.patientms.persistence.enums.Gender;
import com.medical.assessment.patientms.persistence.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    // =========================
    // GET ALL PATIENTS
    // =========================
    @Nested
    @DisplayName("getAllPatients")
    class getAllPatients {
        @Test
        @DisplayName("should get all patients")
        void shouldGetAllPatients() {
            //given
            final Patient patient1 = getPatient(1L);
            final Patient patient2 = getPatient(2L);
            given(patientRepository.findAll()).willReturn(List.of(patient1, patient2));

            //when
            final List<PatientDto> patientDtos = patientService.getAllPatients();

            //then
            verify(patientRepository, times(1)).findAll();
            assertThat(patientDtos).isNotNull();
            assertThat(patientDtos.size()).isEqualTo(2);
            assertThat(patientDtos.get(0).getFirstName()).isEqualTo(patient1.getFirstName());
            assertThat(patientDtos.get(0).getLastName()).isEqualTo(patient1.getLastName());
            assertThat(patientDtos.get(0).getGender().getValue()).isEqualTo(patient1.getGender().getValue());
            assertThat(patientDtos.get(0).getBirthDate()).isEqualTo(patient1.getBirthDate());
            assertThat(patientDtos.get(0).getAddress()).isEqualTo(patient1.getAddress());
            assertThat(patientDtos.get(0).getPhoneNumber()).isEqualTo(patient1.getPhoneNumber());
            assertThat(patientDtos.get(1).getFirstName()).isEqualTo(patient2.getFirstName());
            assertThat(patientDtos.get(1).getLastName()).isEqualTo(patient2.getLastName());
            assertThat(patientDtos.get(1).getGender().getValue()).isEqualTo(patient2.getGender().getValue());
            assertThat(patientDtos.get(1).getBirthDate()).isEqualTo(patient2.getBirthDate());
            assertThat(patientDtos.get(1).getAddress()).isEqualTo(patient2.getAddress());
            assertThat(patientDtos.get(1).getPhoneNumber()).isEqualTo(patient2.getPhoneNumber());
        }

        @Test
        @DisplayName("should return empty list when no patients found")
        void shouldReturnEmptyListWhenNoPatientsFound() {
            //given
            given(patientRepository.findAll()).willReturn(List.of());

            //when
            final List<PatientDto> patientDtos = patientService.getAllPatients();

            //then
            verify(patientRepository, times(1)).findAll();
            assertThat(patientDtos).isNotNull();
            assertThat(patientDtos.size()).isEqualTo(0);
        }
    }

    // =========================
    // GET PATIENT BY ID
    // =========================
    @Nested
    @DisplayName("getPatientById")
    class GetPatientById {

        @Test
        @DisplayName("should get patient by id")
        void shouldGetPatientById() {
            //given
            final Long id = 1L;
            final Patient patient = getPatient(id);
            given(patientRepository.findByPatientId(id)).willReturn(Optional.of(patient));

            //when
            final PatientDto patientDto = patientService.getPatientById(id);

            //then
            verify(patientRepository, times(1)).findByPatientId(id);
            assertThat(patientDto).isNotNull();
            assertThat(patientDto.getFirstName()).isEqualTo(patient.getFirstName());
            assertThat(patientDto.getLastName()).isEqualTo(patient.getLastName());
            assertThat(patientDto.getGender().getValue()).isEqualTo(patient.getGender().getValue());
            assertThat(patientDto.getBirthDate()).isEqualTo(patient.getBirthDate());
            assertThat(patientDto.getAddress()).isEqualTo(patient.getAddress());
            assertThat(patientDto.getPhoneNumber()).isEqualTo(patient.getPhoneNumber());
        }

        @Test
        @DisplayName("should throw PatientNotFoundException when patient not found")
        void shouldThrowPatientNotFoundException() {
            //given
            final Long id = 1L;
            given(patientRepository.findByPatientId(id)).willReturn(Optional.empty());

            //when & then
            final PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(id));
            assertEquals("Patient with id " + id + " not found", exception.getMessage());
            verify(patientRepository, times(1)).findByPatientId(id);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when patient id is null")
        void shouldThrowIllegalArgumentExceptionWhenPatientIdIsNull() {
            //given
            final Long id = null;

            //when & then
            final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> patientService.getPatientById(id));
            assertEquals("Patient id must not be null", exception.getMessage());
            verify(patientRepository, times(0)).findByPatientId(id);
        }
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