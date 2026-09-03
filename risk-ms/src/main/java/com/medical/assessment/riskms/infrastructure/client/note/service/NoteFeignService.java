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

    public List<String> getNoteContentList(final Long patientId) {
        try {
            return noteFeignClient.getNoteContentList(patientId);

        }  catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "unable to verify patient with id " + patientId + e.getMessage());
        }
    }
}
