/**
 * SECTION KEYS
 */
export type SectionKey =
  | 'STYLE_TRADING'
  | 'ACTIFS'
  | 'INDICATEURS_CONFLUENCES'
  | 'REGLES_ACHAT'
  | 'REGLES_VENTE'
  | 'GESTION_RISQUE'
  | 'GESTION_POSITION'
  | 'RISK_REWARD'
  | 'A_EVITER'
  | 'OBJECTIFS'
  | 'PSYCHOLOGIE'
  | 'TEMPS_DISPONIBLE';

export const SECTION_LABELS: Record<SectionKey, string> = {
  STYLE_TRADING: 'Style de trading',
  ACTIFS: 'Actifs',
  INDICATEURS_CONFLUENCES: 'Indicateurs & confluences',
  REGLES_ACHAT: "Règles d'achat",
  REGLES_VENTE: 'Règles de vente',
  GESTION_RISQUE: 'Gestion du risque',
  GESTION_POSITION: 'Gestion de position',
  RISK_REWARD: 'Risk/Reward',
  A_EVITER: 'À éviter',
  OBJECTIFS: 'Objectifs',
  PSYCHOLOGIE: 'Psychologie',
  TEMPS_DISPONIBLE: 'Temps disponible',
};

/**
 * WHAT BACKEND IS WAITING FOR
 */
export interface SectionPayload {
  key: SectionKey;
  content: string;
  comment?: string
}

export interface CustomFieldPayload {
  fieldName: string;
  fieldValue: string;
  displayOrder: number;
  comment?:string;
}

export interface UpdateTradingPlanPayload {
  sections: SectionPayload[];
  customFields: CustomFieldPayload[];
}

/**
 * BACKEND RESPONSES
 */
export interface TradingPlanSectionResponse {
  key: SectionKey;
  content: string | null;
  comment: string | null;
}

export interface TradingPlanCustomFieldResponse {
  id: string;
  fieldName: string;
  fieldValue: string | null;
  comment: string | null;
  displayOrder: number;
}

export interface TradingPlanApiResponse {
  id: string;
  sections: TradingPlanSectionResponse[];
  customFields: TradingPlanCustomFieldResponse[];
  updatedAt: string;
}