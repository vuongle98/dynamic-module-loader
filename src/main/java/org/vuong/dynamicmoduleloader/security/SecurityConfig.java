package org.vuong.dynamicmoduleloader.security;

import java.util.Set;

/**
 * Configuration class for security validation settings.
 * 
 * This class allows customization of security validation rules
 * for different use cases and security requirements.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public class SecurityConfig {

    private final boolean enableValidation;
    private final boolean allowFileOperations;
    private final boolean allowNetworkOperations;
    private final boolean allowReflection;
    private final boolean allowSystemOperations;
    private final boolean allowDatabaseOperations;
    private final int maxStringLength;
    private final int maxMethodComplexity;

    /**
     * Creates a new SecurityConfig with default settings.
     */
    public SecurityConfig() {
        this(true, false, false, false, false, false, 1000, 10);
    }

    /**
     * Creates a new SecurityConfig with custom settings.
     * 
     * @param enableValidation whether to enable security validation
     * @param allowFileOperations whether to allow file operations
     * @param allowNetworkOperations whether to allow network operations
     * @param allowReflection whether to allow reflection
     * @param allowSystemOperations whether to allow system operations
     * @param allowDatabaseOperations whether to allow database operations
     * @param maxStringLength maximum allowed string length
     * @param maxMethodComplexity maximum allowed method complexity
     */
    public SecurityConfig(boolean enableValidation, 
                        boolean allowFileOperations, 
                        boolean allowNetworkOperations, 
                        boolean allowReflection, 
                        boolean allowSystemOperations, 
                        boolean allowDatabaseOperations,
                        int maxStringLength,
                        int maxMethodComplexity) {
        this.enableValidation = enableValidation;
        this.allowFileOperations = allowFileOperations;
        this.allowNetworkOperations = allowNetworkOperations;
        this.allowReflection = allowReflection;
        this.allowSystemOperations = allowSystemOperations;
        this.allowDatabaseOperations = allowDatabaseOperations;
        this.maxStringLength = maxStringLength;
        this.maxMethodComplexity = maxMethodComplexity;
    }

    /**
     * Creates a permissive security configuration for trusted environments.
     * 
     * This configuration allows most operations including file, network,
     * reflection, system, and database operations. Use with caution in
     * untrusted environments.
     * 
     * @return a permissive security configuration
     */
    public static SecurityConfig permissive() {
        return new SecurityConfig(true, true, true, true, true, true, 10000, 50);
    }

    /**
     * Creates a strict security configuration for untrusted environments.
     * 
     * This configuration blocks all potentially dangerous operations
     * including file, network, reflection, system, and database operations.
     * This is the default configuration for maximum security.
     * 
     * @return a strict security configuration
     */
    public static SecurityConfig strict() {
        return new SecurityConfig(true, false, false, false, false, false, 500, 5);
    }

    /**
     * Creates a moderate security configuration for semi-trusted environments.
     * 
     * This configuration provides a balance between security and functionality,
     * blocking dangerous operations while allowing safe ones.
     * 
     * @return a moderate security configuration
     */
    public static SecurityConfig moderate() {
        return new SecurityConfig(true, false, false, false, false, false, 1000, 10);
    }

    // Getters
    
    /**
     * Returns whether security validation is enabled.
     * 
     * @return true if validation is enabled, false otherwise
     */
    public boolean isEnableValidation() { return enableValidation; }
    
    /**
     * Returns whether file operations are allowed.
     * 
     * @return true if file operations are allowed, false otherwise
     */
    public boolean isAllowFileOperations() { return allowFileOperations; }
    
    /**
     * Returns whether network operations are allowed.
     * 
     * @return true if network operations are allowed, false otherwise
     */
    public boolean isAllowNetworkOperations() { return allowNetworkOperations; }
    
    /**
     * Returns whether reflection operations are allowed.
     * 
     * @return true if reflection operations are allowed, false otherwise
     */
    public boolean isAllowReflection() { return allowReflection; }
    
    /**
     * Returns whether system operations are allowed.
     * 
     * @return true if system operations are allowed, false otherwise
     */
    public boolean isAllowSystemOperations() { return allowSystemOperations; }
    
    /**
     * Returns whether database operations are allowed.
     * 
     * @return true if database operations are allowed, false otherwise
     */
    public boolean isAllowDatabaseOperations() { return allowDatabaseOperations; }
    
    /**
     * Returns the maximum allowed string length for source code.
     * 
     * @return the maximum string length
     */
    public int getMaxStringLength() { return maxStringLength; }
    
    /**
     * Returns the maximum allowed method complexity.
     * 
     * @return the maximum method complexity
     */
    public int getMaxMethodComplexity() { return maxMethodComplexity; }
}
