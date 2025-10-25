package org.vuong.dynamicmoduleloader.security;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Security validator for Java source code to prevent malicious code execution.
 * 
 * This validator performs static analysis on Java source code to detect
 * potentially dangerous patterns before compilation and execution.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public class CodeSecurityValidator {

    private CodeSecurityValidator() {
        // private constructor to prevent initialization outside
    }

    // Dangerous imports and classes
    private static final Set<String> DANGEROUS_IMPORTS = Set.of(
        "java.io.File",
        "java.io.FileInputStream",
        "java.io.FileOutputStream",
        "java.nio.file.Files",
        "java.nio.file.Paths",
        "java.lang.Runtime",
        "java.lang.Process",
        "java.lang.ProcessBuilder",
        "java.lang.System",
        "java.net.Socket",
        "java.net.URL",
        "java.net.URLConnection",
        "java.sql.Connection",
        "java.sql.DriverManager",
        "java.util.Properties",
        "java.lang.reflect.Method",
        "java.lang.reflect.Field",
        "java.lang.reflect.Constructor",
        "java.lang.Class",
        "java.lang.ClassLoader",
        "java.security.AccessController",
        "java.security.PrivilegedAction"
    );

    // Dangerous method calls
    private static final Set<String> DANGEROUS_METHODS = Set.of(
        "exec",
        "getRuntime",
        "exit",
        "halt",
        "gc",
        "runFinalization",
        "loadLibrary",
        "load",
        "forName",
        "newInstance",
        "getDeclaredMethod",
        "getMethod",
        "invoke",
        "setAccessible",
        "getDeclaredField",
        "getField",
        "set",
        "get",
        "connect",
        "openConnection",
        "getInputStream",
        "getOutputStream",
        "delete",
        "createNewFile",
        "mkdir",
        "mkdirs",
        "renameTo",
        "listFiles",
        "list",
        "walk",
        "copy",
        "move",
        "deleteIfExists",
        "createDirectories",
        "createFile",
        "write",
        "readAllBytes",
        "readAllLines"
    );

    // Dangerous patterns
    private static final List<Pattern> DANGEROUS_PATTERNS = List.of(
        Pattern.compile("Runtime\\.getRuntime\\(\\)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("ProcessBuilder", Pattern.CASE_INSENSITIVE),
        Pattern.compile("System\\.exit", Pattern.CASE_INSENSITIVE),
        Pattern.compile("System\\.halt", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Class\\.forName", Pattern.CASE_INSENSITIVE),
        Pattern.compile("ClassLoader", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Method\\.invoke", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Field\\.set", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Field\\.get", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bFiles\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bPaths\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bSocket\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bURL\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bURLConnection\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bConnection\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bDriverManager\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bProperties\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\beval\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bscript\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bjavascript\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bvbscript\\b", Pattern.CASE_INSENSITIVE)
    );

    /**
     * Validates Java source code for security threats.
     * 
     * @param sourceCode the Java source code to validate
     * @throws SecurityException if malicious code is detected
     */
    public static void validate(String sourceCode) {
        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            throw new SecurityException("Source code cannot be null or empty");
        }

        // Check for dangerous imports
        validateImports(sourceCode);
        
        // Check for dangerous method calls
        validateMethodCalls(sourceCode);
        
        // Check for dangerous patterns
        validatePatterns(sourceCode);
        
        // Check for suspicious code structures
        validateCodeStructure(sourceCode);
    }

    /**
     * Validates imports in the source code.
     */
    private static void validateImports(String sourceCode) {
        String[] lines = sourceCode.split("\n");
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("import ")) {
                String importStatement = trimmedLine.substring(7).replace(";", "").trim();
                
                for (String dangerousImport : DANGEROUS_IMPORTS) {
                    if (importStatement.contains(dangerousImport)) {
                        throw new SecurityException(
                            "Dangerous import detected: " + importStatement + 
                            ". Import of " + dangerousImport + " is not allowed for security reasons."
                        );
                    }
                }
            }
        }
    }

    /**
     * Validates method calls in the source code.
     */
    private static void validateMethodCalls(String sourceCode) {
        for (String dangerousMethod : DANGEROUS_METHODS) {
            if (sourceCode.contains(dangerousMethod + "(")) {
                throw new SecurityException(
                    "Dangerous method call detected: " + dangerousMethod + 
                    ". This method is not allowed for security reasons."
                );
            }
        }
    }

    /**
     * Validates dangerous patterns in the source code.
     */
    private static void validatePatterns(String sourceCode) {
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(sourceCode).find()) {
                throw new SecurityException(
                    "Dangerous pattern detected: " + pattern.pattern() + 
                    ". This pattern is not allowed for security reasons."
                );
            }
        }
    }

    /**
     * Validates code structure for suspicious patterns.
     */
    private static void validateCodeStructure(String sourceCode) {
        // Check for suspicious comment patterns
        if (sourceCode.contains("/*") && sourceCode.contains("*/")) {
            // Check for hidden code in comments
            String[] parts = sourceCode.split("/\\*");
            for (int i = 1; i < parts.length; i++) {
                String commentPart = parts[i];
                if (commentPart.contains("*/")) {
                    String comment = commentPart.substring(0, commentPart.indexOf("*/"));
                    if (containsSuspiciousContent(comment)) {
                        throw new SecurityException(
                            "Suspicious content detected in comments. Hidden code execution is not allowed."
                        );
                    }
                }
            }
        }
        
        // Check for excessive string concatenation (potential obfuscation)
        if (countOccurrences(sourceCode, "\"") > 50) {
            throw new SecurityException(
                "Excessive string concatenation detected. This may indicate code obfuscation."
            );
        }
        
        // Check for suspicious variable names
        if (sourceCode.matches(".*\\b[a-zA-Z]{1,2}\\b.*")) {
            // Check for single/double letter variables (potential obfuscation)
            String[] words = sourceCode.split("\\s+");
            int suspiciousVars = 0;
            for (String word : words) {
                if (word.matches("\\b[a-zA-Z]{1,2}\\b") && !isKeyword(word)) {
                    suspiciousVars++;
                }
            }
            if (suspiciousVars > 10) {
                throw new SecurityException(
                    "Suspicious variable naming pattern detected. This may indicate code obfuscation."
                );
            }
        }
    }

    /**
     * Checks if content contains suspicious patterns.
     */
    private static boolean containsSuspiciousContent(String content) {
        String lowerContent = content.toLowerCase();
        return lowerContent.contains("exec") || 
               lowerContent.contains("runtime") || 
               lowerContent.contains("process") ||
               lowerContent.contains("system") ||
               lowerContent.contains("file") ||
               lowerContent.contains("socket") ||
               lowerContent.contains("url") ||
               lowerContent.contains("class") ||
               lowerContent.contains("method") ||
               lowerContent.contains("field");
    }

    /**
     * Counts occurrences of a substring in a string.
     */
    private static int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }

    /**
     * Checks if a word is a Java keyword.
     */
    private static boolean isKeyword(String word) {
        Set<String> keywords = Set.of(
            "if", "else", "for", "while", "do", "switch", "case", "default",
            "break", "continue", "return", "try", "catch", "finally", "throw",
            "throws", "public", "private", "protected", "static", "final",
            "abstract", "synchronized", "volatile", "transient", "native",
            "class", "interface", "extends", "implements", "import", "package",
            "new", "this", "super", "instanceof", "void", "int", "long",
            "short", "byte", "char", "float", "double", "boolean", "true",
            "false", "null", "enum", "assert", "const", "goto"
        );
        return keywords.contains(word.toLowerCase());
    }
}
