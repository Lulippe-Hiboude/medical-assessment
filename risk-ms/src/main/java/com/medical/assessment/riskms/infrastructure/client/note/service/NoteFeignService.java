package com.medical.assessment.riskms.infrastructure.client.note.service;

import com.medical.assessment.riskms.infrastructure.client.note.NoteFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteFeignService {
    private final NoteFeignClient noteFeignClient;

    /**
     * Retrieves the contents of all notes associated with a patient.
     *
     * <p>The request is delegated to the note microservice through a Feign
     * client. If the communication with the note service fails, the exception
     * is translated into an HTTP {@code 502 BAD GATEWAY} response.</p>
     *
     * @param patientId the unique identifier of the patient
     * @return a list containing the contents of the patient's notes
     * @throws ResponseStatusException with HTTP status
     *         {@code 502 BAD GATEWAY} if the note service cannot be reached
     *         or returns a Feign client error
     */
    public List<String> getNoteContentList(final Long patientId) {
        try {
            return noteFeignClient.getNoteContentList(patientId);

        }  catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "unable to verify patient with id " + patientId + e.getMessage());
        }
    }
}
