package com.medical.assessment.patientms.service;

import com.medical.assessment.patientms.exception.PatientAlreadyExistsException;
import com.medical.assessment.patientms.exception.PatientNotFoundException;
import com.medical.assessment.patientms.mapper.PatientMapper;
import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.persistence.entity.Patient;
import com.medical.assessment.patientms.persistence.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public PatientDto getPatientById(final Long id) {
        log.debug("Getting patient with id {}", id);
        return ofNullable(id)
                .map(patientRepository::findByPatientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient id must not be null"))
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

    @Transactional
    public PatientDto createPatient(final PatientCreateDto patientCreateDto) {
        log.debug("Creating patient");
        final Patient patient = PatientMapper.INSTANCE.toPatient(patientCreateDto);

        checkDuplicatePatient(patient);

        patientRepository.save(patient);
        return PatientMapper.INSTANCE.toPatientDto(patient);
    }

    private void checkDuplicatePatient(final Patient patient) {
        final List<Patient> existingPatients = findPotentialDuplicates(patient);

        if (CollectionUtils.isNotEmpty(existingPatients)) {
            throw new PatientAlreadyExistsException("Duplicate patient found");
        }
    }

    private List<Patient> findPotentialDuplicates(final Patient patient) {
        return patientRepository.findByLastNameIgnoreCaseAndFirstNameIgnoreCaseAndBirthDateAndGender(
                patient.getLastName(),
                patient.getFirstName(),
                patient.getBirthDate(),
                patient.getGender());
    }
}
