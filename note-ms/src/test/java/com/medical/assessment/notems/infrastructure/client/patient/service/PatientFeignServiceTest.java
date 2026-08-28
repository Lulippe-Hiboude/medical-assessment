package com.medical.assessment.notems.infrastructure.client.patient.service;

import com.medical.assessment.notems.infrastructure.client.patient.PatientFeignClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatientFeignServiceTest {
    @Mock
    private PatientFeignClient patientFeignClient;

    @InjectMocks
    PatientFeignService service;

    //TODO NEED TO CHANGE ENDPOINT RESULT TO BOOLEAN

    @Test
    @DisplayName("should do nothing when patient exists")
    void ShouldDoNothingWhenPatientExists(){
        //given

        //when

        //then
    }

    @Test
    @DisplayName("should throw NOT FOUND exception when patient does not exist")
    void ShouldThrowNotFoundExceptionWhenPatientDoesNotExist(){
        //given

        //when

        //then
    }

    @Test
    @DisplayName("should throw BAD GATEWAY exception when Feign call failed")
    void ShouldThrowBadGatewayExceptionWhenFeignCallFailed(){
        //given

        //when

        //then
    }

}