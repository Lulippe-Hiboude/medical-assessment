package com.medical.assessment.riskms.api.controller;

import com.medical.assessment.risk_ms.risk_assessment.api.RiskApi;
import com.medical.assessment.risk_ms.risk_assessment.model.RiskDto;
import com.medical.assessment.riskms.domain.risk.service.RiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RiskController implements RiskApi {
    final RiskService riskService;

    /**
     * Retrieves the risk profile for a patient.
     *
     * <p>The risk profile is calculated by the risk service based on the
     * patient's medical notes and information. The calculated risk level
     * is returned with HTTP status {@code 200 OK}.</p>
     *
     * @param id the unique identifier of the patient
     * @return a {@link ResponseEntity} containing the patient's {@link RiskDto}
     *         with HTTP status {@code 200 OK}
     * @throws ResponseStatusException if the patient cannot be found or if
     *         a downstream service required to calculate the risk profile
     *         is unavailable
     */
    @Override
    public ResponseEntity<RiskDto> getRiskProfileForPatient(Long id) {
        log.info("determine risk profile for patient with id {}", id);
        return ResponseEntity.ok(riskService.calculatePatientRisk(id));
    }
}
