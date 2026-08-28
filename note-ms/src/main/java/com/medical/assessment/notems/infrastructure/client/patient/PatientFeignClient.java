package com.medical.assessment.notems.infrastructure.client.patient;


import com.medical.assessment.notems.infrastructure.client.patient.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//TODO replace  with data from yaml file
@FeignClient(
        name = "patient-ms",
        url = "http://localhost:8081"

)
//TODO change getPatientById by a boolean ? GET /patient/{id}/exists
public interface PatientFeignClient {
    @GetMapping("/patient/{id}")
    PatientDto getPatientById(@PathVariable("id") final Long id);
}
