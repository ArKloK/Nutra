package com.arklok.nutra.config;

import com.arklok.nutra.constants.UIConstants;

public enum FxmlView {
    LOAD {
        @Override
        public String getFxmlPath() {
            return UIConstants.LOAD_VIEW_PATH;
        }
    },

    HOME {
        @Override
        public String getFxmlPath() {
            return UIConstants.HOME_VIEW_PATH;
        }
    };

    public abstract String getFxmlPath();
}
