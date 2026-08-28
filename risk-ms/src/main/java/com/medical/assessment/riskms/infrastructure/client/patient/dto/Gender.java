package com.medical.assessment.riskms.infrastructure.client.patient.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    F("F"),
    M("M");

    private final String value;
}
