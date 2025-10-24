package org.vuong.dynamicmoduleloader.domain.model;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
public class Plugin implements Serializable {
    private final String name;
    private final Class<?> pluginClass;

    public Plugin(String name, Class<?> pluginClass) {
        this.name = name;
        this.pluginClass = pluginClass;
    }

    public String getName() {
        return name;
    }

    public Class<?> getPluginClass() {
        return pluginClass;
    }
}
