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

    // Texts
    public static final String APP_NAME = "NUTRA";
    public static final String APP_TAGLINE = "Nutrición Inteligente";
    public static final String LOADING_TEXT = "Cargando...";

    // Sizes
    public static final double WINDOW_MIN_WIDTH = 1465;
    public static final double WINDOW_MIN_HEIGHT = 926;

    // Views
    public static final String HOME_VIEW_PATH = "/fxml/home.fxml";
    public static final String LOAD_VIEW_PATH = "/fxml/load.fxml";
    public static final String CONSULTATION_VIEW_PATH = "/fxml/consultation.fxml";
    public static final String CONSULTATION_DETAIL_VIEW_PATH = "/fxml/consultation_detail.fxml";
    public static final String RECIPE_VIEW_PATH = "/fxml/recipe.fxml";
    public static final String RECIPE_DETAIL_VIEW_PATH = "/fxml/recipe_detail.fxml";
    public static final String RECIPE_LIST_VIEW_PATH = "/fxml/recipe_list.fxml";
    public static final String PATIENT_VIEW_PATH = "/fxml/patient.fxml";
    public static final String PATIENT_LIST_VIEW_PATH = "/fxml/patient_list.fxml";
    public static final String PATIENT_DETAIL_VIEW_PATH = "/fxml/patient_detail.fxml";
    public static final String INGREDIENT_VIEW_PATH = "/fxml/ingredient.fxml";
    public static final String INGREDIENT_LIST_VIEW_PATH = "/fxml/ingredient_list.fxml";
}