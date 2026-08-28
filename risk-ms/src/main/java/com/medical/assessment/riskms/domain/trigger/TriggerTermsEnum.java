package com.medical.assessment.riskms.domain.trigger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum TriggerTermsEnum {
    FUMEUR(List.of("fume", "fumer", "fumeur", "fumeuse")),
    ANORMAL(List.of("anormal", "anormale", "anormaux", "anormales")),
    REACTION(List.of("reaction", "reactions")),
    CHOLESTEROL(List.of("cholesterol", "cholesterols")),
    VERTIGES(List.of("vertige", "vertiges")),
    ANTICORPS(List.of("anticorps")),
    HEMOGLOBINE_A1C(List.of("hemoglobine a1c")),
    TAILLE(List.of("taille")),
    POIDS(List.of("poids")),
    MICROALBUMINE(List.of("microalbumine", "microalbumines")),
    RECHUTE(List.of("rechute", "rechutes"));

    private final List<String> variants;

}
