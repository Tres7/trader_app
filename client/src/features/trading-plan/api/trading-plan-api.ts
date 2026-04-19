import { httpClient } from "@/src/shared/api/http-client";
import { TradingPlanApiResponse, UpdateTradingPlanPayload } from "../model/types";

export async function getTradingPlan():Promise<TradingPlanApiResponse> {
    const response = await httpClient.get<TradingPlanApiResponse>('/api/v1/plan');
    return response.data;
}

export async function updateTradingPlan(payload: UpdateTradingPlanPayload): Promise<TradingPlanApiResponse> {
  const response = await httpClient.put<TradingPlanApiResponse>('/api/v1/plan', payload);
  return response.data;
}

export async function exportTradingPlanAsPdf(): Promise<Blob> {
  const response = await httpClient.get('/api/v1/plan/export', {
    params: { format: 'PDF' },
    responseType: 'blob',
  });
  return response.data;
}