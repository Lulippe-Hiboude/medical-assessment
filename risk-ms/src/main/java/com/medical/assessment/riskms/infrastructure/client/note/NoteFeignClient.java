package com.medical.assessment.riskms.infrastructure.client.note;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "${notems.name}",
        url = "${notems.url}"
)
public interface NoteFeignClient {
    @GetMapping("/notes/patient/{id}/note-content")
    List<String> getNoteContentList(@PathVariable("id") final Long id);
}
