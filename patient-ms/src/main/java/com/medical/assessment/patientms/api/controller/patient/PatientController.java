package com.medical.assessment.patientms.api.controller.patient;

import com.medical.assessment.patientms.exception.PatientAlreadyExistsException;
import com.medical.assessment.patientms.exception.PatientNotFoundException;
import com.medical.assessment.patientms.patient.api.PatientApi;
import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.patient.model.PatientRiskProfile;
import com.medical.assessment.patientms.patient.model.PatientUpdateDto;
import com.medical.assessment.patientms.domain.patient.service.PatientService;
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

    /**
     * Retrieves a patient by its unique identifier.
     *
     * <p>The patient information is retrieved from the patient service and
     * returned with HTTP status {@code 200 OK}.</p>
     *
     * @param id the unique identifier of the patient
     * @return a {@link ResponseEntity} containing the requested {@link PatientDto}
     *         with HTTP status {@code 200 OK}
     * @throws PatientNotFoundException if no patient exists with the given identifier
     */
    @Override
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("id") final Long id) {
        log.info("get patient by id");
        final PatientDto patientDto = patientService.getPatientById(id);
        return ResponseEntity.ok(patientDto);
    }

    /**
     * Retrieves the risk profile of a patient.
     *
     * <p>The risk profile (age + gender) is calculated or retrieved by the patient service
     * and returned with HTTP status {@code 200 OK}.</p>
     *
     * @param id the unique identifier of the patient
     * @return a {@link ResponseEntity} containing the patient's
     *         {@link PatientRiskProfile} with HTTP status {@code 200 OK}
     * @throws PatientNotFoundException if no patient exists with the given identifier
     */
    @Override
    public ResponseEntity<PatientRiskProfile> getPatientRiskProfileById(final Long id) {
        log.info("get patient risk profile");
        final PatientRiskProfile patientRiskProfile = patientService.getPatientRiskProfileById(id);
        return ResponseEntity.ok(patientRiskProfile);
    }

    /**
     * Checks whether a patient exists for the given identifier.
     *
     * <p>The existence check is delegated to the patient service and the result
     * is returned with HTTP status {@code 200 OK}.</p>
     *
     * @param id the unique identifier of the patient to check
     * @return a {@link ResponseEntity} containing {@code true} if the patient
     *         exists, {@code false} otherwise, with HTTP status {@code 200 OK}
     */
    @Override
    public ResponseEntity<Boolean> isPatientExists(final Long id) {
        log.info("verify if patient exists for {}", id);
        return ResponseEntity.ok(patientService.isPatientExist(id));
    }

    /**
     * Updates an existing patient's information.
     *
     * <p>The patient is updated using the data provided in the update request
     * and the updated patient is returned with HTTP status {@code 200 OK}.</p>
     *
     * @param id the unique identifier of the patient to update
     * @param patientUpdateDto the data used to update the patient
     * @return a {@link ResponseEntity} containing the updated {@link PatientDto}
     *         with HTTP status {@code 200 OK}
     * @throws PatientNotFoundException if no patient exists with the given identifier
     * @throws IllegalArgumentException if the provided update data is invalid
     */
    @Override
    public ResponseEntity<PatientDto> updatePatient(Long id, PatientUpdateDto patientUpdateDto) {
        log.info("update patient");
        final PatientDto patientDto = patientService.updatePatient(id, patientUpdateDto);
        return ResponseEntity.ok(patientDto);
    }


    /**
     * Creates a new patient.
     *
     * <p>The provided patient data is validated before being passed to the
     * patient service. The created patient is returned with HTTP status
     * {@code 200 OK}.</p>
     *
     * @param patientCreateDto the data required to create the patient
     * @return a {@link ResponseEntity} containing the created {@link PatientDto}
     *         with HTTP status {@code 200 OK}
     * @throws PatientAlreadyExistsException if a patient with the same
     *         identifying information already exists
     * @throws IllegalArgumentException if the provided patient data is invalid
     */
    @Override
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody final PatientCreateDto patientCreateDto) {
        log.info("create patient");
        final PatientDto patientDto = patientService.createPatient(patientCreateDto);
        return ResponseEntity.ok(patientDto);
    }

    /**
     * Retrieves all patients.
     *
     * <p>The list of patients is retrieved from the patient service and
     * returned with HTTP status {@code 200 OK}.</p>
     *
     * @return a {@link ResponseEntity} containing the list of all
     *         {@link PatientDto} instances with HTTP status {@code 200 OK}
     */
    @Override
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        log.info("get all patients");
        final List<PatientDto> patientDtos = patientService.getAllPatients();
        return ResponseEntity.ok(patientDtos);
    }
}
