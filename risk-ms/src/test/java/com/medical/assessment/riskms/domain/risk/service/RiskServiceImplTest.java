package com.medical.assessment.riskms.domain.risk.service;

import com.medical.assessment.risk_ms.risk_assessment.model.RiskDto;
import com.medical.assessment.riskms.infrastructure.client.note.service.NoteFeignService;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.Gender;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.PatientRiskProfileDto;
import com.medical.assessment.riskms.infrastructure.client.patient.service.PatientFeignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskServiceImplTest {

    @Mock
    private PatientFeignService patientFeignService;

    @Mock
    private NoteFeignService noteFeignService;

    @InjectMocks
    private RiskServiceImpl riskService;

    @Test
    @DisplayName("should return NONE when trigger number is equal to 0")
    void shouldReturnNoneWhenTriggerNumberIsEqualTo0() throws Exception {
        //given
        final Long patientId = 1L;
        final String note1 = "Note 1";
        final String note2 = "Note 2";
        final String note3 = "Note 3";
        final List<String> notes = List.of(note1, note2, note3);
        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.NONE);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,never()).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return NONE when trigger number is equal to 1")
    void shouldReturnNoneWhenTriggerNumberIsEqualTo1() throws Exception {
        //given
        final Long patientId = 1L;
        final String note1 = "fumeur";
        final String note2 = "Note 2";
        final String note3 = "Note 3";
        final List<String> notes = List.of(note1, note2, note3);
        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.NONE);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,never()).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return BORDERLINE when trigger number is between 2 and 5 and patient over 30 regardless of gender")
    void shouldReturnBORDERLINE() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 30;
        final String note1 = "fumeur";
        final String note2 = "Hemoglobine A1C";
        final String note3 = "Note 3";
        final List<String> notes = List.of(note1, note2, note3);
        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, Gender.M);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.BORDERLINE);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return IN_DANGER when trigger number is between 6 and 7 and patient over 30 regardless of gender")
    void shouldReturnINDANGER() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 31;
        final String note1 = "fumeur, anormale , reaction";
        final String note2 = "Hemoglobine A1C , taille, poids";
        final String note3 = "rechute";
        final List<String> notes = List.of(note1, note2, note3);
        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, Gender.M);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.IN_DANGER);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }


    @Test
    @DisplayName("should return EARLY_ONSET when trigger number is 8 or more and patient over 30 regardless of gender")
    void shouldReturnEARLYONSET() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 31;
        final String note1 = "fumeur, anormale , reaction";
        final String note2 = "Hemoglobine A1C , taille, poids";
        final String note3 = "rechute, anticorps";
        final List<String> notes = List.of(note1, note2, note3);
        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, Gender.M);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.EARLY_ONSET);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return IN_DANGER when trigger number is  3 or 4 and patient under 30 for a MALE")
    void shouldReturnINDANGERForAMale() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 29;
        final String note1 = "fumeur,";
        final String note2 = "Hemoglobine A1C";
        final String note3 = "rechute, anticorps";
        final List<String> notes = List.of(note1, note2, note3);
        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, Gender.M);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.IN_DANGER);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return IN_DANGER when trigger number is between 4 and 6 and patient under 30 for a FEMALE")
    void shouldReturnINDANGERForAFemale() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 29;
        final Gender gender = Gender.F;
        final String note1 = "fumeur,";
        final String note2 = "Hemoglobine A1C";
        final String note3 = "rechute, anticorps";
        final List<String> notes = List.of(note1, note2, note3);

        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, gender);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.IN_DANGER);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return EARLY_ONSET when trigger number over 5 and patient under 30 for a MALE")
    void shouldReturnEARLYONSETForAMale() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 29;
        final Gender gender = Gender.M;
        final String note1 = "fumeur,";
        final String note2 = "Hemoglobine A1C,reaction";
        final String note3 = "rechute, anticorps";
        final List<String> notes = List.of(note1, note2, note3);

        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, gender);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.EARLY_ONSET);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return EARLY_ONSET when trigger number over 7 and patient under 30 for a FEMALE")
    void shouldReturnEARLYONSETForAFemale() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 29;
        final Gender gender = Gender.F;
        final String note1 = "fumeur,taille, poids";
        final String note2 = "Hemoglobine A1C,reaction";
        final String note3 = "rechute, anticorps";
        final List<String> notes = List.of(note1, note2, note3);

        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, gender);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.EARLY_ONSET);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return UNKNOWN when trigger number equal 2 and patient under 30 for a MALE")
    void shouldReturnUNKNOWNForAMale() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 29;
        final Gender gender = Gender.M;
        final String note1 = "fumeur, ";
        final String note2 = "Hemoglobine A1C";
        final String note3 = "re";
        final List<String> notes = List.of(note1, note2, note3);

        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, gender);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.UNKNOW);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return UNKNOWN when trigger number equal 3 and patient under 30 for a FEMALE")
    void shouldReturnUNKNOWNForAFemale() throws Exception {
        //given
        final Long patientId = 1L;
        final int age = 29;
        final Gender gender = Gender.F;
        final String note1 = "fumeur, ";
        final String note2 = "Hemoglobine A1C";
        final String note3 = "rechute";
        final List<String> notes = List.of(note1, note2, note3);

        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, gender);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.UNKNOW);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,times(1)).getPatientRiskProfile(patientId);
    }

    @Test
    @DisplayName("should return NO_DATA if notes List if empty")
    void shouldReturnNO_DATA() throws Exception {
        //given
        final Long patientId = 1L;
        given(noteFeignService.getNoteContentList(patientId)).willReturn(Collections.emptyList());

        //when
        final RiskDto result = riskService.calculatePatientRisk(patientId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RiskDto.NO_DATA);
        verify(noteFeignService, times(1)).getNoteContentList(patientId);
        verify(patientFeignService,never()).getPatientRiskProfile(patientId);
    }

    private static PatientRiskProfileDto getPatientRiskProfileDto(int age, Gender female) {
        final PatientRiskProfileDto riskProfileDto = new PatientRiskProfileDto();
        riskProfileDto.setAge(age);
        riskProfileDto.setGender(female);
        return riskProfileDto;
    }
}