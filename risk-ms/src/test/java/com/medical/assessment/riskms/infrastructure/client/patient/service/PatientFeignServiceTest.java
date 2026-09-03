package com.medical.assessment.riskms.infrastructure.client.patient.service;

import com.medical.assessment.riskms.infrastructure.client.patient.PatientFeignClient;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.Gender;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.PatientRiskProfileDto;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PatientFeignServiceTest {

    @Mock
    private PatientFeignClient patientFeignClient;

    @InjectMocks
    private PatientFeignService patientFeignService;

    @Test
    @DisplayName("should return patient risk profile successfully")
    void shouldReturnPatientRiskProfileSuccessfully() {
        //given
        final long patientId = 1L;
        final int age = 35;
        final Gender gender = Gender.F;
        final PatientRiskProfileDto patientRiskProfileDto = new PatientRiskProfileDto();
        patientRiskProfileDto.setAge(age);
        patientRiskProfileDto.setGender(gender);

        given(patientFeignClient.getPatientRiskProfileById(patientId)).willReturn(patientRiskProfileDto);

        //when
        final PatientRiskProfileDto expected = patientFeignService.getPatientRiskProfile(patientId);

        //then
        assertThat(expected).isEqualTo(patientRiskProfileDto);
    }

    @Test
    @DisplayName("should throw Response status exception Not Found if a FeignException.NotFound  is catch")
    void shouldThrowAnExceptionIfFeignExceptionIsNotFound() {
        //given
        final Long patientId = 1L;
        final FeignException.NotFound exception =
                mock(FeignException.NotFound.class);

        given(patientFeignClient.getPatientRiskProfileById(patientId))
                .willThrow(exception);

        //when & then
        assertThatThrownBy(() -> patientFeignService.getPatientRiskProfile(patientId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exceptionThrown -> {
                    final ResponseStatusException response =
                            (ResponseStatusException) exceptionThrown;

                    assertThat(response.getStatusCode())
                            .isEqualTo(HttpStatus.NOT_FOUND);

                    assertThat(response.getReason())
                            .isEqualTo("Patient not found with id " + patientId);
                });
    }

    @Test
    @DisplayName("should throw Response status exception Bad Gateway if a FeignException  is catch")
    void shouldThrowAnExceptionIfFeignExceptionIsBadGateway() {
        //given
        final Long patientId = 1L;

        given(patientFeignClient.getPatientRiskProfileById(patientId))
                .willThrow(FeignException.class);

        //when & then
        assertThatThrownBy(() -> patientFeignService.getPatientRiskProfile(patientId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exceptionThrown -> {
                    final ResponseStatusException response =
                            (ResponseStatusException) exceptionThrown;

                    assertThat(response.getStatusCode())
                            .isEqualTo(HttpStatus.BAD_GATEWAY);

                    assertThat(response.getReason())
                            .contains("unable to verify patient with id " + patientId);
                });
    }

}