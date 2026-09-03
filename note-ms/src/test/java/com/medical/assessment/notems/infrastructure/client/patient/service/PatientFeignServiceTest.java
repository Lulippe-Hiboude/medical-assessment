package com.medical.assessment.notems.infrastructure.client.patient.service;

import com.medical.assessment.notems.infrastructure.client.patient.PatientFeignClient;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PatientFeignServiceTest {
    @Mock
    private PatientFeignClient patientFeignClient;

    @InjectMocks
    PatientFeignService service;

    @Test
    @DisplayName("should do nothing when patient exists")
    void ShouldDoNothingWhenPatientExists(){
        //given
        final Long patientId = 1L;
        given(patientFeignClient.doesPatientExist(patientId)).willReturn(Boolean.TRUE);

        //when & then
        assertThatCode(() -> service.verifyPatientExists(patientId))
                .doesNotThrowAnyException();

    }

    @Test
    @DisplayName("should throw NOT FOUND exception when patient does not exist")
    void ShouldThrowNotFoundExceptionWhenPatientDoesNotExist(){
        //given
        final Long patientId = 1L;
        given(patientFeignClient.doesPatientExist(patientId)).willReturn(Boolean.FALSE);

        //when & then
        assertThatThrownBy(() -> service.verifyPatientExists(patientId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    final ResponseStatusException response =
                            (ResponseStatusException) exception;

                    assertThat(response.getStatusCode())
                            .isEqualTo(HttpStatus.NOT_FOUND);

                    assertThat(response.getReason())
                            .isEqualTo("Patient not found with id " + patientId);
                });
    }

    @Test
    @DisplayName("should throw BAD GATEWAY exception when Feign call failed")
    void ShouldThrowBadGatewayExceptionWhenFeignCallFailed(){
        //given
        final Long patientId = 1L;
        given(patientFeignClient.doesPatientExist(patientId)).willThrow(FeignException.class);

        //when & then
        assertThatThrownBy(() -> service.verifyPatientExists(patientId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    Assertions.assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    Assertions.assertThat(responseStatusException.getMessage()).contains("unable to verify patient with id " + patientId);
                });
    }

}