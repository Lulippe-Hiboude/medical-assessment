package com.medical.assessment.patientms.service;

import com.medical.assessment.patientms.exception.PatientAlreadyExistsException;
import com.medical.assessment.patientms.exception.PatientNotFoundException;
import com.medical.assessment.patientms.mapper.PatientMapper;
import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.patient.model.PatientUpdateDto;
import com.medical.assessment.patientms.persistence.entity.Patient;
import com.medical.assessment.patientms.persistence.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public PatientDto getPatientById(final Long id) {
        log.debug("Getting patient with id {}", id);
        final Patient patient = findPatientByPatientId(id);
        return PatientMapper.INSTANCE.toPatientDto(patient);
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

    @Override
    public PatientDto updatePatient(final Long patientId, final PatientUpdateDto patientUpdateDto) {

        if(Objects.isNull(patientUpdateDto)){
            throw new IllegalArgumentException("update payload must not be null");
        }

        normalizeUpdateRequest(patientUpdateDto);

        if(allFieldsNull(patientUpdateDto)){
            throw new IllegalArgumentException("At least one field must be provided");
        }

        final Patient existingPatient = findPatientByPatientId(patientId);
        final Patient updatePatient = PatientMapper.INSTANCE.updatePatient(patientUpdateDto,existingPatient);
        patientRepository.save(updatePatient);

        return PatientMapper.INSTANCE.toPatientDto(updatePatient);
    }

    private void normalizeUpdateRequest(final PatientUpdateDto patientUpdateDto) {
        patientUpdateDto.setFirstName(normalizeStringValue(patientUpdateDto.getFirstName()));
        patientUpdateDto.setLastName(normalizeStringValue(patientUpdateDto.getLastName()));
        patientUpdateDto.setPhoneNumber(normalizeStringValue(patientUpdateDto.getPhoneNumber()));
        patientUpdateDto.setAddress(normalizeStringValue(patientUpdateDto.getAddress()));
    }

    private String normalizeStringValue(final String value){
        if(Objects.isNull(value)){
            return null;
        }
        return StringUtils.normalizeSpace(value);
    }

    private boolean allFieldsNull(final PatientUpdateDto dto) {
       return Stream.of(
               dto.getFirstName(),
               dto.getLastName(),
               dto.getBirthDate(),
               dto.getAddress(),
               dto.getGender(),
               dto.getPhoneNumber()
       ).allMatch(Objects::isNull);
    }

    private Patient findPatientByPatientId(final Long patientId) {
        if (Objects.isNull(patientId)) {
            throw new IllegalArgumentException("Patient id must not be null");
        }

        return patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + patientId + " not found"));
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
