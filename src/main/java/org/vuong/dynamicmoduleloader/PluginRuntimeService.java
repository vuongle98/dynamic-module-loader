package org.vuong.dynamicmoduleloader;

import org.vuong.dynamicmoduleloader.compiler.JavaSourceCompiler;
import org.vuong.dynamicmoduleloader.core.Plugin;
import org.vuong.dynamicmoduleloader.core.PluginRegistry;
import org.vuong.dynamicmoduleloader.registry.InMemoryPluginRegistry;
import org.vuong.dynamicmoduleloader.security.CodeSecurityValidator;
import org.vuong.dynamicmoduleloader.security.EnhancedSecurityValidator;
import org.vuong.dynamicmoduleloader.security.SecurityConfig;
import org.vuong.dynamicmoduleloader.util.PluginNameUtils;

import java.util.Collection;

/**
 * Service responsible for managing plugin lifecycle at runtime.
 * 
 * This service provides functionality to compile Java source code into classes
 * at runtime, register them as plugins, and manage their lifecycle. It maintains
 * an in-memory registry of all loaded plugins and provides thread-safe access
 * to plugin operations.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public class PluginRuntimeService {

    private final JavaSourceCompiler compiler = new JavaSourceCompiler();
    private final PluginRegistry pluginRegistry;
    private final EnhancedSecurityValidator securityValidator;

    /**
     * Creates a new PluginRuntimeService instance with default in-memory registry and strict security.
     */
    public PluginRuntimeService() {
        this(new InMemoryPluginRegistry(), SecurityConfig.strict());
    }

    /**
     * Creates a new PluginRuntimeService instance with the specified registry and strict security.
     * 
     * @param pluginRegistry the registry to use for plugin storage
     * @throws IllegalArgumentException if pluginRegistry is null
     */
    public PluginRuntimeService(PluginRegistry pluginRegistry) {
        this(pluginRegistry, SecurityConfig.strict());
    }

    /**
     * Creates a new PluginRuntimeService instance with the specified registry and security configuration.
     * 
     * @param pluginRegistry the registry to use for plugin storage
     * @param securityConfig the security configuration to use
     * @throws IllegalArgumentException if pluginRegistry or securityConfig is null
     */
    public PluginRuntimeService(PluginRegistry pluginRegistry, SecurityConfig securityConfig) {
        if (pluginRegistry == null) {
            throw new IllegalArgumentException("Plugin registry cannot be null");
        }
        if (securityConfig == null) {
            throw new IllegalArgumentException("Security configuration cannot be null");
        }
        this.pluginRegistry = pluginRegistry;
        this.securityValidator = new EnhancedSecurityValidator(securityConfig);
    }

    /**
     * Compiles Java source code and registers it as a plugin.
     * 
     * This method compiles the provided Java source code into a Class object,
     * creates a Plugin instance, and registers it in the plugin registry.
     * The plugin name is automatically derived from the class name by converting
     * the first character to lowercase.
     * 
     * @param className the name of the class to compile
     * @param javaCode the Java source code as a string
     * @return the registered Plugin instance
     * @throws Exception if compilation fails or the class cannot be instantiated
     * @throws IllegalArgumentException if className or javaCode is null or empty
     */
    public Plugin compileAndRegister(String className, String javaCode) throws Exception {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be null or empty");
        }
        if (javaCode == null || javaCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Java code cannot be null or empty");
        }
        
        // Validate code security before compilation
        securityValidator.validate(javaCode);
        
        Class<?> clazz = compiler.compileFromText(className, javaCode);
        // Verify the class can be instantiated
        clazz.getDeclaredConstructor().newInstance();

        String name = PluginNameUtils.generatePluginName(className);
        Plugin plugin = new Plugin(name, clazz);
        pluginRegistry.register(plugin);
        return plugin;
    }

    /**
     * Retrieves a plugin by its name.
     * 
     * @param name the name of the plugin to retrieve
     * @return the Plugin instance, or null if not found
     * @throws IllegalArgumentException if name is null or empty
     */
    public Plugin getPlugin(String name) {
        return pluginRegistry.getPlugin(name);
    }

    /**
     * Removes a plugin from the registry.
     * 
     * @param name the name of the plugin to remove
     * @return the removed Plugin instance, or null if not found
     * @throws IllegalArgumentException if name is null or empty
     */
    public Plugin removePlugin(String name) {
        return pluginRegistry.removePlugin(name);
    }

    /**
     * Returns all registered plugins.
     * 
     * @return a collection of all registered Plugin instances
     */
    public Collection<Plugin> getAllPlugins() {
        return pluginRegistry.getAllPlugins();
    }

    /**
     * Checks if a plugin with the given name exists.
     * 
     * @param name the name of the plugin to check
     * @return true if the plugin exists, false otherwise
     * @throws IllegalArgumentException if name is null or empty
     */
    public boolean containsPlugin(String name) {
        return pluginRegistry.containsPlugin(name);
    }

    /**
     * Returns the number of registered plugins.
     * 
     * @return the number of registered plugins
     */
    public int getPluginCount() {
        return pluginRegistry.size();
    }

    /**
     * Removes all plugins from the registry.
     */
    public void clearPlugins() {
        pluginRegistry.clear();
    }
}
