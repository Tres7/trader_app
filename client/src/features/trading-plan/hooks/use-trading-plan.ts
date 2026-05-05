import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { getTradingPlan, updateTradingPlan } from "../api/trading-plan-api";
import { UpdateTradingPlanPayload } from "../model/types";
import { downloadAsync, documentDirectory, cacheDirectory, getContentUriAsync } from 'expo-file-system/legacy';
import * as IntentLauncher from 'expo-intent-launcher';
import { Alert, Platform } from 'react-native';
import { getAccessToken } from '@/src/features/auth/store/auth-storage';
import * as Sharing from 'expo-sharing';

export const TRADING_PLAN_QUERY_KEY = ['trading-plan'];

const API_BASE_URL = process.env.EXPO_PUBLIC_API_BASE_URL;

export function useGetTradingPlan() {
  return useQuery({
      queryKey: TRADING_PLAN_QUERY_KEY,
      queryFn: getTradingPlan,
  });
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

type ExportAction = 'preview' | 'download' | 'share';

async function fetchPdf(destination: string): Promise<string> {
  const token = await getAccessToken();
  const result = await downloadAsync(
      `${API_BASE_URL}/api/v1/plan/export?format=PDF`,
      destination,
      { headers: { Authorization: `Bearer ${token}` } }
  );
  return result.uri;
}


export function useExportTradingPlan() {
  return useMutation({
      mutationFn: async (action: ExportAction) => {
        const fileName = `plan-trading-${Date.now()}.pdf`;

        if (action === 'download') {
          const uri = await fetchPdf((cacheDirectory ?? '') + fileName);
          return { action, uri };
        }
        const uri = await fetchPdf((cacheDirectory ?? '') + fileName);
        return { action, uri };
      },
      onSuccess: async ({ action, uri }) => {
        const canShare = await Sharing.isAvailableAsync();

        if (action === 'preview') {
            if (Platform.OS === 'android') {
                const contentUri = await getContentUriAsync(uri);
                await IntentLauncher.startActivityAsync('android.intent.action.VIEW', {
                  data: contentUri,
                  flags: 1,
                  type: 'application/pdf',
                });
            } else {
                if (canShare) {
                  await Sharing.shareAsync(uri, {
                    mimeType: 'application/pdf',
                    UTI: 'com.adobe.pdf',
                  });
                }
            }
        } else if (action === 'share') {
          if (canShare) {
            await Sharing.shareAsync(uri, {
              mimeType: 'application/pdf',
              UTI: 'com.adobe.pdf',
            });
          }
        } else if (action === 'download') {
          const canShare = await Sharing.isAvailableAsync();
          if (canShare) {
            await Sharing.shareAsync(uri, {
                mimeType: 'application/pdf',
                UTI: 'com.adobe.pdf',
                dialogTitle: 'Enregistrer le PDF',
            });
          }
        }
      },
      onError: () => {
        console.error('Export PDF error:', Error);
        Alert.alert('Erreur', 'Impossible de générer le PDF. Veuillez réessayer.');
      },
  });
}
