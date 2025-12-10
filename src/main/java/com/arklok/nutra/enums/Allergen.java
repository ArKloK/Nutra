package com.arklok.nutra.enums;

public enum Allergen {
    GLUTEN("Gluten"),
    CRUSTACEANS("Crustaceos"),
    MILK("Leche"),
    EGGS("Huevos"),
    FISH("Pescado"),
    MOLLUSCS("Moluscos"),
    NUTS("Nueces"),
    SOY("Soja"),
    SESAME("Sésamo"),
    SULPHITES("Sulfitos"),
    MUSTARD("Mostaza"),
    CELERY("Apio"),
    LUPIN("Lupino");

    private final String label;
    Allergen(String label) { this.label = label; }
    @Override
    public String toString() { return label; }
}
