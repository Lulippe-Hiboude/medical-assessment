package com.medical.assessment.riskms.domain.risk.service;

import com.medical.assessment.risk_ms.risk_assessment.model.RiskDto;

public interface RiskService {
    RiskDto calculatePatientRisk(final Long patientId);
}
