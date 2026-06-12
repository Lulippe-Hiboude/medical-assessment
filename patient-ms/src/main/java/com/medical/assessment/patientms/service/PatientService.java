package com.medical.assessment.patientms.service;

import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;

import java.util.List;

public interface PatientService {
    PatientDto getPatientById(Long id);

    List<PatientDto> getAllPatients();

    PatientDto createPatient(PatientCreateDto patientCreateDto);
}
