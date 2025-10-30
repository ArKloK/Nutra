package com.arklok.nutra.config;

public enum FxmlView {
    LOGIN {
        @Override
        public String getFxmlPath() {
            return "/fxml/login.fxml";
        }
    },

    LOAD {
        @Override
        public String getFxmlPath() {
            return "/fxml/load.fxml";
        }
    },

    HOME {
        @Override
        public String getFxmlPath() {
            return "/fxml/home.fxml";
        }
    },

    SCALES {
        @Override
        public String getFxmlPath() {
            return "/fxml/scales.fxml";
        }
    },

    INTERVALS {
        @Override
        public String getFxmlPath() {
            return "/fxml/intervals.fxml";
        }
    },

    SCALES_THEORY {
        @Override
        public String getFxmlPath() {
            return "/fxml/scales-theory.fxml";
        }
    };

    public abstract String getFxmlPath();
}
