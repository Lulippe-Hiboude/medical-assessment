package com.medical.assessment.patientms.persistence.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    FEMALE("FEMALE"),
    MALE("MALE");

    private final String gender;
}
