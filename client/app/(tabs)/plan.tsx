import React, { useEffect, useState } from 'react';
import {
  View, ScrollView, Modal, TextInput,
  KeyboardAvoidingView, Platform,
  TouchableOpacity, TouchableWithoutFeedback,
  ActivityIndicator,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Eye, FileText, Share2, Plus, Pencil, Trash2, X, MessageSquare } from 'lucide-react-native';

import { Text } from '@/src/shared/ui/primitives/text';
import { Button } from '@/src/shared/ui/primitives/button';
import { Separator } from '@/src/shared/ui/primitives/separator';
import { Icon } from '@/src/shared/ui/primitives/icon';
import {
  Accordion, AccordionContent,
  AccordionItem, AccordionTrigger,
} from '@/src/shared/ui/primitives/accordion';
import { useGetTradingPlan, useUpdateTradingPlan } from '@/src/features/trading-plan/hooks/use-trading-plan';
import {
  SECTION_LABELS, SectionKey,
  TradingPlanCustomFieldResponse,
} from '@/src/features/trading-plan/model/types';
import { PlanBottomSheet } from '@/src/features/trading-plan/ui/plan-bottom-sheet';

const ALL_SECTIONS: SectionKey[] = [
  'STYLE_TRADING', 'ACTIFS', 'INDICATEURS_CONFLUENCES',
  'REGLES_ACHAT', 'REGLES_VENTE', 'GESTION_RISQUE',
  'GESTION_POSITION', 'RISK_REWARD', 'A_EVITER',
  'OBJECTIFS', 'PSYCHOLOGIE', 'TEMPS_DISPONIBLE',
];

type EditingField =
  | { type: 'section'; key: SectionKey; label: string }
  | { type: 'custom'; id: string; label: string };

export default function PlanScreen() {
    const { data, isLoading } = useGetTradingPlan();
    const { mutate: savePlan, isPending } = useUpdateTradingPlan();
    const [editingFieldName, setEditingFieldName] = useState('');


    const [drafts, setDrafts] = useState<Record<string, string>>({});
    const [customFields, setCustomFields] = useState<TradingPlanCustomFieldResponse[]>([]);
    const [editing, setEditing] = useState<EditingField | null>(null);
    const [editingValue, setEditingValue] = useState('');
    const [sectionComments, setSectionComments] = useState<Record<string, string>>({});
    const [commentEditing, setCommentEditing] = useState<{ key: string; label: string } | null>(null);
    const [commentValue, setCommentValue] = useState('');


    useEffect(() => {
        if (!data) {
            return
        };
        const map: Record<string, string> = {};
        const comments: Record<string, string> = {};
        data.sections.forEach((s) => { 
            map[s.key] = s.content ?? '';
            comments[s.key] = s.comment ?? ''; 
        });
        setSectionComments(comments);
        setDrafts(map);
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
            prev.map((f) => f.id === editing.id ? { ...f, fieldName: editingFieldName, fieldValue: editingValue } : f)
        );
        }
        setEditing(null);
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

    if (isLoading) {
        return (
        <View className="flex-1 items-center justify-center bg-background">
            <ActivityIndicator />
        </View>
        );
    }

    return (
        <SafeAreaView className="flex-1 bg-background">

            {/* Header */}
            <View className="flex-row items-center justify-between px-4 pt-2 pb-4">
                <Text className="text-2xl font-bold text-foreground">Mon Plan de Trading</Text>
                <View className="flex-row gap-4">
                <TouchableOpacity><Icon as={Eye} size={22} className="text-muted-foreground" /></TouchableOpacity>
                <TouchableOpacity><Icon as={FileText} size={22} className="text-muted-foreground" /></TouchableOpacity>
                <TouchableOpacity><Icon as={Share2} size={22} className="text-muted-foreground" /></TouchableOpacity>
                </View>
            </View>

            {/* Save button */}
            <View className="px-4 pb-4 items-center">
                <Button onPress={handleSave} disabled={isPending}>
                <Text>{isPending ? 'Enregistrement...' : 'Enregistrer le plan'}</Text>
                </Button>
            </View>

            <ScrollView className="flex-1 px-4" keyboardShouldPersistTaps="handled">

                {/* Sections standard */}
                <Accordion type="multiple" defaultValue={[]}>
                {ALL_SECTIONS.map((key) => (
                    <AccordionItem key={key} value={key}>
                    <AccordionTrigger>
                        <View className="flex-1">
                        <Text className="font-semibold text-foreground">{SECTION_LABELS[key]}</Text>
                        {drafts[key] ? (
                            <Text className="text-xs text-muted-foreground mt-0.5">
                            {drafts[key]}
                            </Text>
                        ) : null}
                        </View>
                    </AccordionTrigger>
                    <AccordionContent>
                        <View className="flex-row items-start justify-between gap-3">
                            <Text className="flex-1 text-sm text-foreground">
                                {drafts[key] || <Text className="text-muted-foreground">Non renseigné</Text>}
                            </Text>
                            <View className="flex-row gap-3">
                                <TouchableOpacity
                                    onPress={() => {
                                    setCommentEditing({ key, label: SECTION_LABELS[key] });
                                    setCommentValue(sectionComments[key] ?? '');
                                    }}>
                                    <Icon
                                    as={MessageSquare}
                                    size={16}
                                    className={sectionComments[key] ? 'text-green-500' : 'text-muted-foreground'}
                                    />
                                </TouchableOpacity>
                                <TouchableOpacity
                                    onPress={() => openEdit(
                                    { type: 'section', key, label: SECTION_LABELS[key] },
                                    drafts[key] ?? ''
                                    )}>
                                    <Icon as={Pencil} size={16} className="text-muted-foreground" />
                                </TouchableOpacity>
                            </View>
                            
                        </View>
                    </AccordionContent>
                    </AccordionItem>
                ))}
                </Accordion>

                <Separator className="my-2" />

                {/* Champs personnalisés */}
                <View className="flex-row items-center justify-between mb-3">
                    <Text className="font-semibold text-foreground text-base">Sections personnalisées</Text>
                    <TouchableOpacity onPress={addCustomField}>
                        <Icon as={Plus} size={20} className="text-muted-foreground" />
                    </TouchableOpacity>
                </View>

                <Accordion type="multiple" defaultValue={[]}>
                    {customFields.map((field) => (
                        <AccordionItem key={field.id} value={field.id}>
                            <AccordionTrigger>
                                <View className="flex-1">
                                    <Text className="font-semibold text-foreground">
                                        {field.fieldName || 'Nouveau champ'}
                                    </Text>
                                    {field.fieldValue ? (
                                        <Text className="text-xs text-muted-foreground mt-0.5">
                                        {field.fieldValue}
                                        </Text>
                                    ) : null}
                                </View>
                            </AccordionTrigger>
                            <AccordionContent>
                                <View className="flex-row items-start justify-between gap-3">
                                    <Text className="flex-1 text-sm text-foreground">
                                        {field.fieldValue || <Text className="text-muted-foreground">Non renseigné</Text>}
                                    </Text>
                                    <View className="flex-row gap-3">
                                        <TouchableOpacity
                                            onPress={() => {
                                                setEditing({ type: 'custom', id: field.id, label: field.fieldName });
                                                setEditingValue(field.fieldValue ?? '');
                                                setEditingFieldName(field.fieldName);
                                            }}>
                                            <Icon as={Pencil} size={16} className="text-muted-foreground" />
                                        </TouchableOpacity>
                                        <TouchableOpacity onPress={() => removeCustomField(field.id)}>
                                            <Icon as={Trash2} size={16} className="text-muted-foreground" />
                                        </TouchableOpacity>
                                    </View>
                                </View>
                            </AccordionContent>
                        </AccordionItem>
                    ))}
                </Accordion>
                <View className="h-10" />
            </ScrollView>

            {/* Bottom sheet Modal */}
            <PlanBottomSheet
                visible={!!editing}
                title={editing?.label ?? ''}
                value={editingValue}
                onClose={() => setEditing(null)}
                onChangeValue={setEditingValue}
                onConfirm={confirmEdit}
                fieldName={editing?.type === 'custom' ? editingFieldName : undefined}
                onChangeFieldName={editing?.type === 'custom' ? setEditingFieldName : undefined}
            />

            <PlanBottomSheet
                visible={!!commentEditing}
                title={`Commentaire — ${commentEditing?.label ?? ''}`}
                value={commentValue}
                onClose={() => setCommentEditing(null)}
                onChangeValue={setCommentValue}
                confirmLabel="Enregistrer le commentaire"
                placeholder="Ajouter un commentaire..."
                onConfirm={() => {
                    if (!commentEditing) return;
                    setSectionComments((prev) => ({ ...prev, [commentEditing.key]: commentValue }));
                    setCommentEditing(null);
                }}
            />
        </SafeAreaView>
    );
}
