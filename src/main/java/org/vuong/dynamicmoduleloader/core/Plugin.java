package org.vuong.dynamicmoduleloader.core;

import lombok.*;

import java.io.Serializable;

/**
 * Represents a dynamically loaded plugin in the system.
 * A Plugin encapsulates a compiled Java class that was loaded at runtime,
 * along with metadata about the plugin such as its name and the actual
 * Class object that can be instantiated.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
@Setter
@Getter
public class Plugin implements Serializable {
    
    /**
     * The name of the plugin, typically derived from the class name.
     * This name is used for registry lookups and plugin management.
     */
    private final String name;
    
    /**
     * The compiled Class object representing this plugin.
     * This can be used to instantiate the plugin or access its methods.
     */
    private final Class<?> pluginClass;

    /**
     * Creates a new Plugin instance.
     * 
     * @param name the name of the plugin
     * @param pluginClass the compiled Class object for this plugin
     * @throws IllegalArgumentException if name or pluginClass is null
     */
    public Plugin(String name, Class<?> pluginClass) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin name cannot be null or empty");
        }
        if (pluginClass == null) {
            throw new IllegalArgumentException("Plugin class cannot be null");
        }
        this.name = name;
        this.pluginClass = pluginClass;
    }
}
