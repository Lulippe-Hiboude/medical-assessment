package com.medical.assessment.riskms.infrastructure.client.patient.service;

import com.medical.assessment.riskms.infrastructure.client.patient.PatientFeignClient;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.PatientRiskProfileDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PatientFeignService {
    private final PatientFeignClient patientFeignClient;

    public PatientRiskProfileDto getPatientRiskProfile(final Long patientId) {
        try {
            return patientFeignClient.getPatientRiskProfileById(patientId);

        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Patient not found with id " + patientId);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "unable to verify patient with id " + patientId + e.getMessage());
        }
    }
}
