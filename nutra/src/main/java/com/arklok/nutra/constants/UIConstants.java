package com.arklok.nutra.constants;

public final class UIConstants {

    private UIConstants() {
        throw new AssertionError("Cannot instantiate UIConstants");
    }

    // Colors
    public static final String COLOR_PRIMARY = "#626D71";
    public static final String COLOR_BACKGROUND = "#CDCDC0";
    public static final String COLOR_ACCENT = "#DDBC95";
    public static final String COLOR_SECONDARY = "#B38867";

    // Styles
    public static final String STYLE_BACKGROUND = "-fx-background-color: " + COLOR_BACKGROUND + ";";
    public static final String STYLE_PRIMARY_TEXT = "-fx-fill: " + COLOR_PRIMARY + ";";

    // Texts
    public static final String APP_NAME = "NUTRA";
    public static final String APP_TAGLINE = "Nutrición Inteligente";
    public static final String LOADING_TEXT = "Cargando...";

    // Sizes
    public static final double WINDOW_MIN_WIDTH = 1465;
    public static final double WINDOW_MIN_HEIGHT = 926;
}