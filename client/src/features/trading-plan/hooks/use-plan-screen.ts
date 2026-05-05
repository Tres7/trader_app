import { useEffect, useState } from 'react';
import { SectionKey, TradingPlanCustomFieldResponse } from '../model/types';
import { useExportTradingPlan, useGetTradingPlan, useUpdateTradingPlan } from './use-trading-plan';

export const ALL_SECTIONS: SectionKey[] = [
  'STYLE_TRADING', 'ACTIFS', 'INDICATEURS_CONFLUENCES',
  'REGLES_ACHAT', 'REGLES_VENTE', 'GESTION_RISQUE',
  'GESTION_POSITION', 'RISK_REWARD', 'A_EVITER',
  'OBJECTIFS', 'PSYCHOLOGIE', 'TEMPS_DISPONIBLE',
];

export type EditingField =
  | { type: 'section'; key: SectionKey; label: string }
  | { type: 'custom'; id: string; label: string };

export function usePlanScreen() {
    const { data, isLoading } = useGetTradingPlan();
    const { mutate: savePlan, isPending: isSaving } = useUpdateTradingPlan();
    const { mutate: exportPlan, isPending: isExporting } = useExportTradingPlan();

    const [drafts, setDrafts] = useState<Record<string, string>>({});
    const [customFields, setCustomFields] = useState<TradingPlanCustomFieldResponse[]>([]);
    const [sectionComments, setSectionComments] = useState<Record<string, string>>({});

    const [editing, setEditing] = useState<EditingField | null>(null);
    const [editingValue, setEditingValue] = useState('');
    const [editingFieldName, setEditingFieldName] = useState('');

    const [commentEditing, setCommentEditing] = useState<{ key: string; label: string } | null>(null);
    const [commentValue, setCommentValue] = useState('');

    useEffect(() => {
        if (!data) return;
        const map: Record<string, string> = {};
        const comments: Record<string, string> = {};
        data.sections.forEach((s) => {
        map[s.key] = s.content ?? '';
        comments[s.key] = s.comment ?? '';
        });
        setDrafts(map);
        setSectionComments(comments);
        setCustomFields(data.customFields);
    }, [data]);

    function openEdit(field: EditingField, currentValue: string) {
        setEditing(field);
        setEditingValue(currentValue);
    }

    function confirmEdit() {
        if (!editing) return;
        if (editing.type === 'section') {
        setDrafts((prev) => ({ ...prev, [editing.key]: editingValue }));
        } else {
        setCustomFields((prev) =>
            prev.map((f) =>
            f.id === editing.id ? { ...f, fieldName: editingFieldName, fieldValue: editingValue } : f
            )
        );
        }
        setEditing(null);
    }

    function openCommentEdit(key: string, label: string) {
        setCommentEditing({ key, label });
        setCommentValue(sectionComments[key] ?? '');
    }

    function confirmComment() {
        if (!commentEditing) return;
        setSectionComments((prev) => ({ ...prev, [commentEditing.key]: commentValue }));
        setCommentEditing(null);
    }

    function addCustomField() {
        const field: TradingPlanCustomFieldResponse = {
        id: `new-${Date.now()}`,
        fieldName: '',
        fieldValue: '',
        comment: null,
        displayOrder: customFields.length + 1,
        };
        setCustomFields((prev) => [...prev, field]);
        setEditing({ type: 'custom', id: field.id, label: 'Nouvelle section' });
        setEditingValue('');
        setEditingFieldName('');
    }

    function closeEdit() {
        if (editing?.type === 'custom' && editing.id.startsWith('new-')) {
            setCustomFields((prev) => prev.filter((f) => f.id !== editing.id));
        }
        setEditing(null);
    }


    function removeCustomField(id: string) {
        setCustomFields((prev) => prev.filter((f) => f.id !== id));
    }

    function handleSave() {
        savePlan({
        sections: ALL_SECTIONS.map((key) => ({ key, content: drafts[key] ?? '' })),
        customFields: customFields.map((f, i) => ({
            fieldName: f.fieldName,
            fieldValue: f.fieldValue ?? '',
            displayOrder: i + 1,
        })),
        });
    }

    return {
        isLoading,
        isSaving,
        isExporting,
        drafts,
        customFields,
        sectionComments,
        editing,
        editingValue,
        editingFieldName,
        setEditingValue,
        setEditingFieldName,
        openEdit,
        confirmEdit,
        closeEdit,
        commentEditing,
        commentValue,
        setCommentValue,
        openCommentEdit,
        confirmComment,
        closeCommentEdit: () => setCommentEditing(null),
        addCustomField,
        removeCustomField,
        handleSave,
        exportPlan,
    };
}
