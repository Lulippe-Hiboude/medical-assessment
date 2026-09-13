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

    /**
     * Retrieves the risk-related information of a patient.
     *
     * <p>The request is delegated to the patient microservice through a
     * Feign client. A missing patient is translated into an HTTP
     * {@code 404 NOT FOUND} response, while other Feign communication
     * errors are translated into an HTTP {@code 502 BAD GATEWAY} response.</p>
     *
     * @param patientId the unique identifier of the patient
     * @return the patient's {@link PatientRiskProfileDto}
     * @throws ResponseStatusException with HTTP status
     *         {@code 404 NOT FOUND} if the patient does not exist
     * @throws ResponseStatusException with HTTP status
     *         {@code 502 BAD GATEWAY} if the patient service cannot be reached
     *         or returns another Feign client error
     */
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
