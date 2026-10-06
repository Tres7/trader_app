import { buildPlanSummary } from '../lib/plan-summary';
import { isPlanNotFoundError, useGetTradingPlan } from './use-trading-plan';

/**
 * Short overview of the trading plan for the home screen.
 * Shares the trading plan query cache, so it does not trigger an extra request.
 */
export function usePlanSummary() {
  const { data, error, isLoading } = useGetTradingPlan();
  const items = buildPlanSummary(data);

  return {
    items,
    isLoading,
    isEmpty: !isLoading && (isPlanNotFoundError(error) || (!!data && items.length === 0)),
    isError: !!error && !isPlanNotFoundError(error),
  };
}
