import { SECTION_LABELS, SectionKey, TradingPlanApiResponse } from '../model/types';

export const SUMMARY_SECTIONS: SectionKey[] = ['ACTIFS', 'STYLE_TRADING', 'RISK_REWARD'];

export interface PlanSummaryItem {
  key: SectionKey;
  label: string;
  value: string;
}

/**
 * Filled summary sections of the plan, in display order. Empty sections are left out.
 */
export function buildPlanSummary(plan: TradingPlanApiResponse | undefined): PlanSummaryItem[] {
  if (!plan) return [];

  return SUMMARY_SECTIONS.flatMap((key) => {
    const value = plan.sections.find((section) => section.key === key)?.content?.trim();
    return value ? [{ key, label: SECTION_LABELS[key], value }] : [];
  });
}
