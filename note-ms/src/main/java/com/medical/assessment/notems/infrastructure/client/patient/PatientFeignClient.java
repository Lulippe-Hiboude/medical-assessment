package com.medical.assessment.notems.infrastructure.client.patient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "${patientms.name}",
        url = "${patientms.url}"

)
public interface PatientFeignClient {
    @GetMapping("/patient/{id}/exists")
    boolean doesPatientExist(@PathVariable("id") final Long id);
}
