import { describe, expect, it } from '@jest/globals';

import { TradingPlanApiResponse, TradingPlanSectionResponse } from '../../model/types';
import { buildPlanSummary } from '../plan-summary';

function plan(sections: TradingPlanSectionResponse[]): TradingPlanApiResponse {
  return { id: 'plan-1', sections, customFields: [], updatedAt: '2026-10-06T10:00:00Z' };
}

describe('buildPlanSummary', () => {
  it('returns nothing without a plan', () => {
    expect(buildPlanSummary(undefined)).toEqual([]);
  });

  it('keeps the summary sections in display order with their labels', () => {
    const summary = buildPlanSummary(
      plan([
        { key: 'RISK_REWARD', content: '1:2 minimum', comment: null },
        { key: 'A_EVITER', content: 'Trader les news', comment: null },
        { key: 'ACTIFS', content: 'EURUSD, GBPUSD', comment: null },
        { key: 'STYLE_TRADING', content: 'Breakout, Retest', comment: null },
      ])
    );

    expect(summary).toEqual([
      { key: 'ACTIFS', label: 'Actifs', value: 'EURUSD, GBPUSD' },
      { key: 'STYLE_TRADING', label: 'Style de trading', value: 'Breakout, Retest' },
      { key: 'RISK_REWARD', label: 'Risk/Reward', value: '1:2 minimum' },
    ]);
  });

  it('skips empty, blank and missing sections and trims values', () => {
    const summary = buildPlanSummary(
      plan([
        { key: 'ACTIFS', content: '  XAUUSD \n', comment: null },
        { key: 'STYLE_TRADING', content: '   ', comment: null },
        { key: 'RISK_REWARD', content: null, comment: 'à définir' },
      ])
    );

    expect(summary).toEqual([{ key: 'ACTIFS', label: 'Actifs', value: 'XAUUSD' }]);
  });
});
