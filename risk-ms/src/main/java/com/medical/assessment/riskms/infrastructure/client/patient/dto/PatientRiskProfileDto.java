package com.medical.assessment.riskms.infrastructure.client.patient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class PatientRiskProfileDto {
    private Integer age;
    private Gender gender;
}
