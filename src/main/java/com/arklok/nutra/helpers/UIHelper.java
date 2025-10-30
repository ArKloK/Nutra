package com.arklok.nutra.helpers;

import com.arklok.nutra.config.PrimaryStageHolder;
import com.arklok.nutra.constants.UIConstants;

public class UIHelper {
    public static void InitializeUI(){
        PrimaryStageHolder.getPrimaryStage().setMinWidth(UIConstants.WINDOW_MIN_WIDTH);
        PrimaryStageHolder.getPrimaryStage().setMinHeight(UIConstants.WINDOW_MIN_HEIGHT);
        PrimaryStageHolder.getPrimaryStage().setWidth(UIConstants.WINDOW_MIN_WIDTH);
        PrimaryStageHolder.getPrimaryStage().setHeight(UIConstants.WINDOW_MIN_HEIGHT);
        PrimaryStageHolder.getPrimaryStage().centerOnScreen();
    }
}
