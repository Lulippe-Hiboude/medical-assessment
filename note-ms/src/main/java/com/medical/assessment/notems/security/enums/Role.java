package com.medical.assessment.notems.security.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
    ORGANIZER("ORGANIZER"),
    DOCTOR("DOCTOR");

    private final String value;

    public static Role fromValue(final String roleValue) {
        if (StringUtils.isBlank(roleValue)) {
            throw new IllegalArgumentException("Role is missing in the token");
        }

        return Arrays.stream(Role.values())
                .filter(role -> role.getValue().equals(roleValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role value: " + roleValue));
    }
}
