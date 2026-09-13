package com.medical.assessment.riskms.domain.risk.service;

import com.medical.assessment.risk_ms.risk_assessment.model.RiskDto;
import com.medical.assessment.riskms.domain.trigger.TriggerDetector;
import com.medical.assessment.riskms.domain.trigger.TriggerTermsEnum;
import com.medical.assessment.riskms.infrastructure.client.note.service.NoteFeignService;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.Gender;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.PatientRiskProfileDto;
import com.medical.assessment.riskms.infrastructure.client.patient.service.PatientFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskServiceImpl implements RiskService {
    private final PatientFeignService patientFeignService;
    private final NoteFeignService noteFeignService;
    final int MALE_IN_DANGER_MIN_TERMS_NUMBER = 3;
    final int MALE_EARLY_ON_SET_MIN_TERMS_NUMBER = 5;
    final int FEMALE_IN_DANGER_MIN_TERMS_NUMBER = 4;
    final int FEMALE_EARLY_ON_SET_MIN_TERMS_NUMBER = 7;

    /**
     * Calculates the medical risk profile of a patient.
     *
     * <p>The risk is determined from the patient's medical note contents
     * by detecting predefined trigger terms. If no notes are available,
     * the risk level is {@link RiskDto#NO_DATA}. When trigger terms are
     * detected, the patient's age and gender are used to determine the
     * appropriate risk level.</p>
     *
     * <p>For patients aged 30 or older, the risk level is determined from
     * the number of detected trigger terms. For patients under 30, the
     * risk thresholds depend on the patient's gender.</p>
     *
     * @param patientId the unique identifier of the patient
     * @return the calculated {@link RiskDto} representing the patient's
     *         medical risk level
     * @throws ResponseStatusException if the patient's notes cannot be
     *         retrieved or if the patient's information cannot be retrieved
     *         from the patient service
     */
    @Override
    public RiskDto calculatePatientRisk(final Long patientId) {
        final List<String> noteList = noteFeignService.getNoteContentList(patientId);

        if (noteList.isEmpty()) {
            log.info("No Note found for patient id {}", patientId);
            return RiskDto.NO_DATA;
        }

        final Integer numberOfTriggerTerms = calculateNumberOfTriggerTerms(noteList);

        if (numberOfTriggerTerms <= 1) {
            return RiskDto.NONE;
        }

        final PatientRiskProfileDto patientRiskProfile = patientFeignService.getPatientRiskProfile(patientId);

        if (patientRiskProfile.getAge() >= 30) {
            return resolveRiskOver30(numberOfTriggerTerms);
        }

        return patientRiskProfile.getGender().equals(Gender.M)
                ? resolveRiskUnder30(numberOfTriggerTerms, MALE_IN_DANGER_MIN_TERMS_NUMBER, MALE_EARLY_ON_SET_MIN_TERMS_NUMBER)
                : resolveRiskUnder30(numberOfTriggerTerms, FEMALE_IN_DANGER_MIN_TERMS_NUMBER, FEMALE_EARLY_ON_SET_MIN_TERMS_NUMBER);
    }

    private static RiskDto resolveRiskOver30(Integer numberOfTriggerTerms) {
        switch (numberOfTriggerTerms) {
            case 2, 3, 4, 5 -> {
                return RiskDto.BORDERLINE;
            }
            case 6, 7 -> {
                return RiskDto.IN_DANGER;
            }
            default -> {
                return RiskDto.EARLY_ONSET;
            }
        }
    }

    private RiskDto resolveRiskUnder30(final Integer numberOfTriggerTerms, final int inDangerMinTermsNumber, final int earlyOnSetMinTermsNumber) {
        if (numberOfTriggerTerms >= earlyOnSetMinTermsNumber) {
            return RiskDto.EARLY_ONSET;
        }

        if (numberOfTriggerTerms >= inDangerMinTermsNumber) {
            return RiskDto.IN_DANGER;
        }
        return RiskDto.UNKNOWN;
    }

    private Integer calculateNumberOfTriggerTerms(final List<String> noteList) {
        final Set<TriggerTermsEnum> numberOfTriggerTerms = TriggerDetector.detectTriggerTerms(noteList);

        return numberOfTriggerTerms.size();
    }
}
