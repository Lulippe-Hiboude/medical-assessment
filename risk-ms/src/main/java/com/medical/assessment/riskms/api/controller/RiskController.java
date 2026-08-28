package com.medical.assessment.riskms.api.controller;

import com.medical.assessment.risk_ms.risk_assessment.api.RiskApi;
import com.medical.assessment.risk_ms.risk_assessment.model.RiskDto;
import com.medical.assessment.riskms.domain.risk.service.RiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RiskController implements RiskApi {
    final RiskService riskService;

    @Override
    public ResponseEntity<RiskDto> getRiskProfileForPatient(Long id) {
        log.info("determine risk profile for patient with id {}", id);
        return ResponseEntity.ok(riskService.calculatePatientRisk(id));
    }
}
