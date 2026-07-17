package com.medical.assessment.notems.infrastructure.client.patient.service;

import com.medical.assessment.notems.infrastructure.client.patient.PatientFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PatientFeignService {
    private final PatientFeignClient patientFeignClient;

    public void verifyPatientExists(final Long patientId) {
        try {
            patientFeignClient.getPatientById(patientId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Patient not found with id " + patientId + e.getMessage());
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "unable to verify patient with id " + patientId + e.getMessage());
        }
    }
}
