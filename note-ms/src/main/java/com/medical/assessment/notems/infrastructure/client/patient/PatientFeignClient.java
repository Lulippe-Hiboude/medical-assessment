package com.medical.assessment.notems.infrastructure.client.patient;


import com.medical.assessment.notems.infrastructure.client.patient.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "patient-ms",
        url = "http://localhost:8081"

)
public interface PatientFeignClient {
    @GetMapping("/patient/{id}")
    PatientDto getPatientById(@PathVariable("id") final Long id);
}
