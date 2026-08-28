package com.medical.assessment.notems.security.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    @DisplayName("should throw IAE if roleValue is missing")
    void ShouldThrowIAEIfRoleValueIsMissing(){
        //when & then
        assertThrows(IllegalArgumentException.class, () -> Role.fromValue(""));
    }

    @Test
    @DisplayName("should throw IAE if roleValue is invalid")
    void ShouldThrowIAEIfRoleValueIsInvalid(){
        //when & then
        assertThrows(IllegalArgumentException.class, () -> Role.fromValue("invalidRole"));
    }
}