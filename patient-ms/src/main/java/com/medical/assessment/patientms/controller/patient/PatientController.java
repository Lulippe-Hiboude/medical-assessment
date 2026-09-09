package com.medical.assessment.patientms.controller.patient;

import com.medical.assessment.patientms.patient.api.PatientApi;
import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.patient.model.PatientRiskProfile;
import com.medical.assessment.patientms.patient.model.PatientUpdateDto;
import com.medical.assessment.patientms.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class PatientController implements PatientApi {
    private final PatientService patientService;

    @Override
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("id") final Long id) {
        log.info("get patient by id");
        final PatientDto patientDto = patientService.getPatientById(id);
        return ResponseEntity.ok(patientDto);
    }

    @Override
    public ResponseEntity<PatientRiskProfile> getPatientRiskProfileById(final Long id) {
        log.info("get patient risk profile");
        final PatientRiskProfile patientRiskProfile = patientService.getPatientRiskProfileById(id);
        return ResponseEntity.ok(patientRiskProfile);
    }

    @Override
    public ResponseEntity<Boolean> isPatientExists(final Long id) {
        log.info("verify if patient exists for {}", id);
        return ResponseEntity.ok(patientService.isPatientExist(id));
    }

    @Override
    public ResponseEntity<PatientDto> updatePatient(Long id, PatientUpdateDto patientUpdateDto) {
        log.info("update patient");
        final PatientDto patientDto = patientService.updatePatient(id, patientUpdateDto);
        return ResponseEntity.ok(patientDto);
    }


    @Override
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody final PatientCreateDto patientCreateDto) {
        log.info("create patient");
        final PatientDto patientDto = patientService.createPatient(patientCreateDto);
        return ResponseEntity.ok(patientDto);
    }

    @Override
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        log.info("get all patients");
        final List<PatientDto> patientDtos = patientService.getAllPatients();
        return ResponseEntity.ok(patientDtos);
    }
}
