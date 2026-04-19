import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { getTradingPlan, updateTradingPlan } from "../api/trading-plan-api";
import { UpdateTradingPlanPayload } from "../model/types";

export const TRADING_PLAN_QUERY_KEY = ['trading-plan'];

export function useGetTradingPlan() {
    return useQuery({
        queryKey: TRADING_PLAN_QUERY_KEY,
        queryFn:  getTradingPlan
    })
}

export function useUpdateTradingPlan() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: UpdateTradingPlanPayload) => updateTradingPlan(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: TRADING_PLAN_QUERY_KEY });
    },
  });
}