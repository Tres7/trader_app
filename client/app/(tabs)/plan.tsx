import React from 'react';
import { View, ScrollView, ActivityIndicator, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Eye, Share2, Plus, Pencil, Trash2, MessageSquare } from 'lucide-react-native';

import { Text } from '@/src/shared/ui/primitives/text';
import { Button } from '@/src/shared/ui/primitives/button';
import { Separator } from '@/src/shared/ui/primitives/separator';
import { Icon } from '@/src/shared/ui/primitives/icon';
import {
  Accordion, AccordionContent,
  AccordionItem, AccordionTrigger,
} from '@/src/shared/ui/primitives/accordion';
import { SECTION_LABELS } from '@/src/features/trading-plan/model/types';
import { PlanBottomSheet } from '@/src/features/trading-plan/ui/plan-bottom-sheet';
import { usePlanScreen, ALL_SECTIONS } from '@/src/features/trading-plan/hooks/use-plan-screen';
import { PlanAccordionItem } from '@/src/features/trading-plan/ui/plan-accordion-item';


export default function PlanScreen() {
  const {
    isLoading, isSaving, isExporting,
    drafts, customFields, sectionComments,
    editing, editingValue, editingFieldName,
    setEditingValue, setEditingFieldName,
    openEdit, confirmEdit, closeEdit,
    commentEditing, commentValue, setCommentValue,
    openCommentEdit, confirmComment, closeCommentEdit,
    addCustomField, removeCustomField,
    handleSave, exportPlan, customFieldComments,
    openCustomFieldCommentEdit,
  } = usePlanScreen();

  if (isLoading) {
    return (
      <View className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator />
      </View>
    );
  }

  return (
    <SafeAreaView className="flex-1 bg-background">

      <View className="flex-row items-center justify-between px-4 pt-2 pb-4">
        <Text className="text-2xl font-bold text-foreground">Mon Plan de Trading</Text>
        <View className="flex-row gap-4">
          <TouchableOpacity onPress={() => exportPlan('preview')} disabled={isExporting}>
            <Icon as={Eye} size={22} className="text-muted-foreground" />
          </TouchableOpacity>
          <TouchableOpacity onPress={() => exportPlan('share')} disabled={isExporting}>
            <Icon as={Share2} size={22} className="text-muted-foreground" />
          </TouchableOpacity>
        </View>
      </View>

      <View className="px-4 pb-4 items-center">
        <Button onPress={handleSave} disabled={isSaving}>
          <Text>{isSaving ? 'Enregistrement...' : 'Enregistrer le plan'}</Text>
        </Button>
      </View>

      <ScrollView className="flex-1 px-4" keyboardShouldPersistTaps="handled">

        <Accordion type="multiple" defaultValue={[]}>
            {ALL_SECTIONS.map((key) => (
                <PlanAccordionItem
                key={key}
                value={key}
                title={SECTION_LABELS[key]}
                preview={drafts[key]}
                content={drafts[key]}
                actions={
                    <>
                        <TouchableOpacity onPress={() => openCommentEdit(key, SECTION_LABELS[key])}>
                            <Icon
                            as={MessageSquare}
                            size={16}
                            className={sectionComments[key] ? 'text-green-500' : 'text-muted-foreground'}
                            />
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => openEdit({ type: 'section', key, label: SECTION_LABELS[key] }, drafts[key] ?? '')}>
                            <Icon as={Pencil} size={16} className="text-muted-foreground" />
                        </TouchableOpacity>
                    </>
                }
                />
            ))}
        </Accordion>

        <Separator className="my-2" />

        <View className="flex-row items-center justify-between mb-3">
          <Text className="font-semibold text-foreground text-base">Sections personnalisées</Text>
          <TouchableOpacity onPress={addCustomField}>
            <Icon as={Plus} size={20} className="text-muted-foreground" />
          </TouchableOpacity>
        </View>

        <Accordion type="multiple" defaultValue={[]}>
            {customFields.map((field) => (
                <PlanAccordionItem
                key={field.id}
                value={field.id}
                title={field.fieldName || 'Nouveau champ'}
                preview={field.fieldValue}
                content={field.fieldValue}
                actions={
                    <>
                      <TouchableOpacity onPress={() => openCustomFieldCommentEdit(field.id, field.fieldName)}>
                        <Icon
                          as={MessageSquare}
                          size={16}
                          className={customFieldComments[field.id] ? 'text-green-500' : 'text-muted-foreground'}
                        />
                      </TouchableOpacity>
                      <TouchableOpacity
                          onPress={() => {
                          openEdit({ type: 'custom', id: field.id, label: field.fieldName }, field.fieldValue ?? '');
                          setEditingFieldName(field.fieldName);
                          }}>
                          <Icon as={Pencil} size={16} className="text-muted-foreground" />
                      </TouchableOpacity>
                      <TouchableOpacity onPress={() => removeCustomField(field.id)}>
                          <Icon as={Trash2} size={16} className="text-muted-foreground" />
                      </TouchableOpacity>
                    </>
                }
                />
            ))}
        </Accordion>

        <View className="h-10" />
      </ScrollView>

      <PlanBottomSheet
        visible={!!editing}
        title={editing?.label ?? ''}
        value={editingValue}
        onClose={closeEdit}
        onChangeValue={setEditingValue}
        onConfirm={confirmEdit}
        fieldName={editing?.type === 'custom' ? editingFieldName : undefined}
        onChangeFieldName={editing?.type === 'custom' ? setEditingFieldName : undefined}
      />

      <PlanBottomSheet
        visible={!!commentEditing}
        title={`Commentaire — ${commentEditing?.label ?? ''}`}
        value={commentValue}
        onClose={closeCommentEdit}
        onChangeValue={setCommentValue}
        confirmLabel="Enregistrer le commentaire"
        placeholder="Ajouter un commentaire..."
        onConfirm={confirmComment}
      />
    </SafeAreaView>
  );
}
