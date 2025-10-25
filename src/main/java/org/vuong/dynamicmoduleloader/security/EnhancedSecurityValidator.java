package org.vuong.dynamicmoduleloader.security;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Enhanced security validator that uses configuration-based validation rules.
 * 
 * This validator provides more flexible security validation based on
 * configurable security policies.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public class EnhancedSecurityValidator {

    private final SecurityConfig config;

    /**
     * Creates a new EnhancedSecurityValidator with the specified security configuration.
     * 
     * @param config the security configuration to use for validation
     * @throws IllegalArgumentException if config is null
     */
    public EnhancedSecurityValidator(SecurityConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Security configuration cannot be null");
        }
        this.config = config;
    }

    /**
     * Validates Java source code based on the security configuration.
     * 
     * @param sourceCode the Java source code to validate
     * @throws SecurityException if malicious code is detected
     */
    public void validate(String sourceCode) {
        if (!config.isEnableValidation()) {
            return;
        }

        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            throw new SecurityException("Source code cannot be null or empty");
        }

        // Check string length
        if (sourceCode.length() > config.getMaxStringLength()) {
            throw new SecurityException(
                "Source code exceeds maximum allowed length: " + config.getMaxStringLength()
            );
        }

        // Validate based on configuration
        validateFileOperations(sourceCode);
        validateNetworkOperations(sourceCode);
        validateReflectionOperations(sourceCode);
        validateSystemOperations(sourceCode);
        validateDatabaseOperations(sourceCode);
        validateCodeComplexity(sourceCode);
    }

    private void validateFileOperations(String sourceCode) {
        if (!config.isAllowFileOperations()) {
            String[] filePatterns = {
                "java.io.File", "java.nio.file.Files", "java.nio.file.Paths",
                "FileInputStream", "FileOutputStream", "FileReader", "FileWriter"
            };
            
            for (String pattern : filePatterns) {
                if (sourceCode.contains(pattern)) {
                    throw new SecurityException(
                        "File operations are not allowed: " + pattern
                    );
                }
            }
        }
    }

    private void validateNetworkOperations(String sourceCode) {
        if (!config.isAllowNetworkOperations()) {
            String[] networkPatterns = {
                "java.net.Socket", "java.net.URL", "java.net.URLConnection",
                "java.net.HttpURLConnection", "java.net.ServerSocket"
            };
            
            for (String pattern : networkPatterns) {
                if (sourceCode.contains(pattern)) {
                    throw new SecurityException(
                        "Network operations are not allowed: " + pattern
                    );
                }
            }
        }
    }

    private void validateReflectionOperations(String sourceCode) {
        if (!config.isAllowReflection()) {
            String[] reflectionPatterns = {
                "java.lang.reflect", "Class.forName", "getDeclaredMethod",
                "getMethod", "invoke", "getDeclaredField", "getField",
                "setAccessible", "newInstance"
            };
            
            for (String pattern : reflectionPatterns) {
                if (sourceCode.contains(pattern)) {
                    throw new SecurityException(
                        "Reflection operations are not allowed: " + pattern
                    );
                }
            }
        }
    }

    private void validateSystemOperations(String sourceCode) {
        if (!config.isAllowSystemOperations()) {
            String[] systemPatterns = {
                "Runtime.getRuntime", "ProcessBuilder", "System.exit",
                "System.halt", "System.gc", "System.runFinalization"
            };
            
            for (String pattern : systemPatterns) {
                if (sourceCode.contains(pattern)) {
                    throw new SecurityException(
                        "System operations are not allowed: " + pattern
                    );
                }
            }
        }
    }

    private void validateDatabaseOperations(String sourceCode) {
        if (!config.isAllowDatabaseOperations()) {
            String[] databasePatterns = {
                "java.sql.Connection", "java.sql.DriverManager",
                "java.sql.Statement", "java.sql.PreparedStatement"
            };
            
            for (String pattern : databasePatterns) {
                if (sourceCode.contains(pattern)) {
                    throw new SecurityException(
                        "Database operations are not allowed: " + pattern
                    );
                }
            }
        }
    }

    private void validateCodeComplexity(String sourceCode) {
        // Count method complexity (simplified)
        int methodCount = countOccurrences(sourceCode, "public ") + 
                         countOccurrences(sourceCode, "private ") + 
                         countOccurrences(sourceCode, "protected ");
        
        if (methodCount > config.getMaxMethodComplexity()) {
            throw new SecurityException(
                "Code complexity exceeds maximum allowed: " + config.getMaxMethodComplexity() + 
                " methods (found: " + methodCount + ")"
            );
        }
    }

    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}
