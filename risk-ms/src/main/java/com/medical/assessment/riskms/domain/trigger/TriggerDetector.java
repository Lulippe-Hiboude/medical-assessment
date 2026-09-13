package com.medical.assessment.riskms.domain.trigger;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class TriggerDetector {
    private static final Map<TriggerTermsEnum, List<Pattern>> PATTERNS_BY_TERMS = buildPatterns();
    public static final String WORD_BOUNDARY = "\\b";
    public static final String COMBINING_MARKS_REGEX = "\\p{M}";

    /**
     * Detects predefined medical trigger terms in a list of note contents.
     *
     * <p>Each note is normalized by removing diacritical marks and converting
     * its content to lowercase. The normalized content is then compared
     * against the configured variants of each {@link TriggerTermsEnum}.</p>
     *
     * <p>Each detected trigger term is added only once to the returned set,
     * even if it occurs multiple times or in multiple notes.</p>
     *
     * @param noteContentList the list of medical note contents to analyze
     * @return a {@link Set} containing the distinct trigger terms detected
     *         in the provided notes
     */
    public static Set<TriggerTermsEnum> detectTriggerTerms(final List<String> noteContentList) {
        Set<TriggerTermsEnum> triggers = new HashSet<>();

        for (String noteContent : noteContentList) {
            final String normalizedNoteContent = normalizeContent(noteContent);
            System.out.println("CONTENT : ["+normalizedNoteContent+ "]");

            for (TriggerTermsEnum term : TriggerTermsEnum.values()) {
                final List<Pattern> patterns = PATTERNS_BY_TERMS.get(term);

                if(matchesAnyPattern(patterns, normalizedNoteContent)) {
                    triggers.add(term);
                }
            }
        }
        return triggers;
    }

    private static boolean matchesAnyPattern(List<Pattern> patterns, String normalizedNoteContent) {
        return patterns.stream()
                .anyMatch(p -> p.matcher(normalizedNoteContent).find());
    }

    private static String normalizeContent(final String content) {
        return  Normalizer.normalize(content, Normalizer.Form.NFD)
                .replaceAll(COMBINING_MARKS_REGEX, StringUtils.EMPTY)
                .toLowerCase();
    }

    private static Map<TriggerTermsEnum, List<Pattern>> buildPatterns() {
        Map<TriggerTermsEnum, List<Pattern>> map = new HashMap<>();

        for (TriggerTermsEnum terms : TriggerTermsEnum.values()) {
            final List<Pattern> patterns = terms.getVariants().stream()
                    .map(v -> Pattern.compile(WORD_BOUNDARY + v + WORD_BOUNDARY))
                    .toList();
            map.put(terms, patterns);
        }

        return Collections.unmodifiableMap(map);
    }
}
