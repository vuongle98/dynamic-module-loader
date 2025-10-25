package org.vuong.dynamicmoduleloader.registry;

import org.vuong.dynamicmoduleloader.core.Plugin;
import org.vuong.dynamicmoduleloader.core.PluginRegistry;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the PluginRegistry interface.
 * 
 * This implementation uses a ConcurrentHashMap to provide thread-safe
 * storage and retrieval of plugins. It's suitable for applications that
 * don't require persistent storage of plugins.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public class InMemoryPluginRegistry implements PluginRegistry {

    private final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();

    /**
     * Default constructor for InMemoryPluginRegistry.
     * Initializes the internal plugin storage.
     */
    public InMemoryPluginRegistry() {
        // default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Plugin register(Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        return plugins.put(plugin.getName(), plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Plugin getPlugin(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin name cannot be null or empty");
        }
        return plugins.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Plugin removePlugin(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin name cannot be null or empty");
        }
        return plugins.remove(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Plugin> getAllPlugins() {
        return plugins.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsPlugin(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin name cannot be null or empty");
        }
        return plugins.containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return plugins.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        plugins.clear();
    }
}
