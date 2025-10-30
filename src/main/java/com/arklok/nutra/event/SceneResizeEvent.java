package com.arklok.nutra.event;

import org.springframework.context.ApplicationEvent;

public class SceneResizeEvent extends ApplicationEvent {
    private final Number sceneWidth;

    public SceneResizeEvent(Object source, Number sceneWidth) {
        super(source);
        this.sceneWidth = sceneWidth;
    }

    public Number getSceneWidth() {
        return sceneWidth;
    }
}
