package org.vuong.dynamicmoduleloader;

import org.vuong.dynamicmoduleloader.compiler.JavaPluginCompiler;
import org.vuong.dynamicmoduleloader.core.Plugin;
import org.vuong.dynamicmoduleloader.core.PluginRegistry;
import org.vuong.dynamicmoduleloader.registry.InMemoryPluginRegistry;
import org.vuong.dynamicmoduleloader.security.EnhancedSecurityValidator;
import org.vuong.dynamicmoduleloader.security.SecurityConfig;
import org.vuong.dynamicmoduleloader.util.PluginNameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

/**
 * Service responsible for loading plugins from Java source files.
 * 
 * This service provides functionality to compile and load Java source files
 * from the filesystem into Plugin instances. It maintains a registry of
 * loaded plugins and provides plugin management capabilities.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public class PluginLoadService {

    private final JavaPluginCompiler compiler = new JavaPluginCompiler();
    private final PluginRegistry pluginRegistry;
    private final EnhancedSecurityValidator securityValidator;

    /**
     * Creates a new PluginLoadService instance with default in-memory registry and strict security.
     */
    public PluginLoadService() {
        this(new InMemoryPluginRegistry(), SecurityConfig.strict());
    }

    /**
     * Creates a new PluginLoadService instance with the specified registry and strict security.
     * 
     * @param pluginRegistry the registry to use for plugin storage
     * @throws IllegalArgumentException if pluginRegistry is null
     */
    public PluginLoadService(PluginRegistry pluginRegistry) {
        this(pluginRegistry, SecurityConfig.strict());
    }

    /**
     * Creates a new PluginLoadService instance with the specified registry and security configuration.
     * 
     * @param pluginRegistry the registry to use for plugin storage
     * @param securityConfig the security configuration to use
     * @throws IllegalArgumentException if pluginRegistry or securityConfig is null
     */
    public PluginLoadService(PluginRegistry pluginRegistry, SecurityConfig securityConfig) {
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
     * Loads a plugin from a Java source file and registers it.
     * 
     * This method compiles the Java source file, creates a Plugin instance
     * from the resulting class, and registers it in the plugin registry.
     * The plugin name is automatically derived from the class name by converting
     * the first character to lowercase.
     * 
     * @param javaFilePath the path to the Java source file
     * @return the loaded and registered Plugin instance
     * @throws Exception if compilation fails or the file cannot be read
     * @throws IllegalArgumentException if javaFilePath is null or empty
     */
    public Plugin loadAndRegisterPlugin(String javaFilePath) throws Exception {
        if (javaFilePath == null || javaFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Java file path cannot be null or empty");
        }
        
        File javaFile = new File(javaFilePath);
        if (!javaFile.exists()) {
            throw new IllegalArgumentException("Java file does not exist: " + javaFilePath);
        }
        if (!javaFile.getName().endsWith(".java")) {
            throw new IllegalArgumentException("File must be a Java source file (.java): " + javaFilePath);
        }
        
        // Read and validate file content for security
        String fileContent = readFileContent(javaFile);
        securityValidator.validate(fileContent);
        
        Class<?> clazz = compiler.compileAndLoad(javaFile);
        // Verify the class can be instantiated
        clazz.getDeclaredConstructor().newInstance();

        String name = PluginNameUtils.generatePluginName(clazz.getSimpleName());
        Plugin plugin = new Plugin(name, clazz);
        pluginRegistry.register(plugin);
        return plugin;
    }

    /**
     * Loads a plugin from a Java source file without registering it.
     * 
     * This method compiles the Java source file and creates a Plugin instance
     * from the resulting class, but does not register it in the plugin registry.
     * 
     * @param javaFilePath the path to the Java source file
     * @return the loaded Plugin instance (not registered)
     * @throws Exception if compilation fails or the file cannot be read
     * @throws IllegalArgumentException if javaFilePath is null or empty
     */
    public Plugin loadPlugin(String javaFilePath) throws Exception {
        if (javaFilePath == null || javaFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Java file path cannot be null or empty");
        }
        
        File javaFile = new File(javaFilePath);
        if (!javaFile.exists()) {
            throw new IllegalArgumentException("Java file does not exist: " + javaFilePath);
        }
        if (!javaFile.getName().endsWith(".java")) {
            throw new IllegalArgumentException("File must be a Java source file (.java): " + javaFilePath);
        }
        
        // Read and validate file content for security
        String fileContent = readFileContent(javaFile);
        securityValidator.validate(fileContent);
        
        Class<?> clazz = compiler.compileAndLoad(javaFile);
        // Verify the class can be instantiated
        clazz.getDeclaredConstructor().newInstance();

        String name = PluginNameUtils.generatePluginName(clazz.getSimpleName());
        return new Plugin(name, clazz);
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

    /**
     * Reads the content of a Java file for security validation.
     * 
     * @param javaFile the Java file to read
     * @return the file content as a string
     * @throws IOException if the file cannot be read
     */
    private String readFileContent(File javaFile) throws IOException {
        return Files.readString(javaFile.toPath());
    }
}
