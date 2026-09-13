export type  RiskLevel =
    'NONE' |
    'BORDERLINE' |
    'IN_DANGER' |
    'EARLY_ONSET' |
    'NO_DATA' |
    'UNKNOWN';

export const RISK_LABELS : Record<RiskLevel, string> = {
    NONE: 'Aucun risque',
    BORDERLINE: 'Risque limité',
    IN_DANGER: 'Danger',
    EARLY_ONSET: 'Apparition précoce',
    NO_DATA: 'Pas de données pour calculer le risque',
    UNKNOWN: 'Cas non couvert'
}
