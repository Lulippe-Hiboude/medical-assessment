package com.medical.assessment.riskms.infrastructure.client.patient;

import com.medical.assessment.riskms.infrastructure.client.patient.dto.PatientRiskProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "patient-ms",
        url = "http://localhost:8081"
)
public interface PatientFeignClient {

    @GetMapping("/patient/{id}/risk-profile")
    PatientRiskProfileDto getPatientRiskProfileById(@PathVariable("id") final Long id);
}
