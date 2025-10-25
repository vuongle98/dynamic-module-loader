package org.vuong.dynamicmoduleloader.core;

import java.util.Collection;

/**
 * Registry interface for managing plugin storage and retrieval.
 * 
 * This interface defines the contract for plugin storage operations,
 * allowing for different implementations (in-memory, persistent, etc.).
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public interface PluginRegistry {

    /**
     * Registers a plugin in the registry.
     * 
     * @param plugin the plugin to register
     * @return the previously registered plugin with the same name, or null if none existed
     * @throws IllegalArgumentException if plugin is null
     */
    Plugin register(Plugin plugin);

    /**
     * Retrieves a plugin by its name.
     * 
     * @param name the name of the plugin to retrieve
     * @return the plugin, or null if not found
     * @throws IllegalArgumentException if name is null or empty
     */
    Plugin getPlugin(String name);

    /**
     * Removes a plugin from the registry.
     * 
     * @param name the name of the plugin to remove
     * @return the removed plugin, or null if not found
     * @throws IllegalArgumentException if name is null or empty
     */
    Plugin removePlugin(String name);

    /**
     * Returns all registered plugins.
     * 
     * @return a collection of all registered plugins
     */
    Collection<Plugin> getAllPlugins();

    /**
     * Checks if a plugin with the given name exists.
     * 
     * @param name the name of the plugin to check
     * @return true if the plugin exists, false otherwise
     * @throws IllegalArgumentException if name is null or empty
     */
    boolean containsPlugin(String name);

    /**
     * Returns the number of registered plugins.
     * 
     * @return the number of registered plugins
     */
    int size();

    /**
     * Removes all plugins from the registry.
     */
    void clear();
}
