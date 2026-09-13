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

    /**
     * Verifies that a patient exists in the patient service.
     *
     * <p>The verification is performed through the patient service using
     * a Feign client. If the patient does not exist, a {@code 404 NOT FOUND}
     * response status exception is thrown. If the patient service cannot
     * be reached or returns a Feign error, a {@code 502 BAD GATEWAY}
     * response status exception is thrown.</p>
     *
     * @param patientId the unique identifier of the patient to verify
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if
     *         the patient does not exist
     * @throws ResponseStatusException with {@link HttpStatus#BAD_GATEWAY} if
     *         the patient cannot be verified because of a Feign client error
     */
    public void verifyPatientExists(final Long patientId) {
        try {
            final boolean exist =  patientFeignClient.doesPatientExist(patientId);

            if (!exist) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Patient not found with id " + patientId);
            }
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "unable to verify patient with id " + patientId + e.getMessage());
        }
    }
}
