package com.medical.assessment.patientms.service;

import com.medical.assessment.patientms.exception.PatientNotFoundException;
import com.medical.assessment.patientms.mapper.PatientMapper;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.persistence.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientDto getPatientById(final Long id) {
        log.debug("Getting patient with id {}", id);
        return ofNullable(id)
                .map(patientRepository::findByPatientId)
                .orElseThrow(()-> new IllegalArgumentException("Patient id must not be null"))
                .map(PatientMapper.INSTANCE::toPatientDto)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found"));
    }

    public List<PatientDto> getAllPatients() {
        log.debug("Getting all patients");
        return patientRepository.findAll()
                .stream()
                .map(PatientMapper.INSTANCE::toPatientDto)
                .toList();
    }
}
