package com.medical.assessment.patientms.controller.patient;

import com.medical.assessment.patientms.patient.api.PatientApi;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class PatientController implements PatientApi {
    private final PatientService patientService;

    @Override
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("id") @Positive final Long id) {
        log.info("get patient id");
        final PatientDto patientDto = patientService.getPatientById(id);
        return ResponseEntity.ok(patientDto);
    }

    @Override
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        log.info("get all patients");
        final List<PatientDto> patientDtos = patientService.getAllPatients();
        return ResponseEntity.ok(patientDtos);
    }
}
