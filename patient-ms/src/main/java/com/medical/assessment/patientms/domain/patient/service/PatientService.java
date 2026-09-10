package com.medical.assessment.patientms.domain.patient.service;

import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.patient.model.PatientRiskProfile;
import com.medical.assessment.patientms.patient.model.PatientUpdateDto;

import java.util.List;

public interface PatientService {
    PatientDto getPatientById(Long id);

    List<PatientDto> getAllPatients();

    PatientDto createPatient(PatientCreateDto patientCreateDto);

    PatientDto updatePatient(Long patientId ,PatientUpdateDto patientUpdateDto);

    PatientRiskProfile getPatientRiskProfileById(Long patientId);

    Boolean isPatientExist(Long patientId);
}
