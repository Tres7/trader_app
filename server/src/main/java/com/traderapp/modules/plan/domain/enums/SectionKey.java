package com.traderapp.modules.plan.domain.enums;

public enum SectionKey {
    STYLE_TRADING("Style de trading"),
    ACTIFS("Actifs"),
    INDICATEURS_CONFLUENCES("Indicateurs & confluences"),
    REGLES_ACHAT("Règles d'achat"),
    REGLES_VENTE("Règles de vente"),
    GESTION_RISQUE("Gestion du risque"),
    GESTION_POSITION("Gestion de position"),
    RISK_REWARD("Risk/Reward"),
    A_EVITER("À éviter"),
    OBJECTIFS("Objectifs"),
    PSYCHOLOGIE("Psychologie"),
    TEMPS_DISPONIBLE("Temps disponible");

    private final String label;

    SectionKey(String label) { 
        this.label = label; 
    }

    public String getLabel() { 
        return label; 
    }
}
