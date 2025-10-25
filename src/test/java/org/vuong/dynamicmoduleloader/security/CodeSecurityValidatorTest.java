package org.vuong.dynamicmoduleloader.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeSecurityValidatorTest {

    @Test
    void validate_safeCode_passes() {
        String safeCode = """
            public class SafePlugin {
                public int add(int a, int b) {
                    return a + b;
                }
            }
            """;
        
        assertDoesNotThrow(() -> CodeSecurityValidator.validate(safeCode));
    }

    @Test
    void validate_maliciousRuntimeCode_throwsException() {
        String maliciousCode = """
            public class MaliciousPlugin {
                public void executeCommand() {
                    Runtime.getRuntime().exec("rm -rf /");
                }
            }
            """;
        
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> CodeSecurityValidator.validate(maliciousCode));
        
        assertTrue(exception.getMessage().contains("Dangerous method call detected"));
    }

    @Test
    void validate_maliciousFileOperations_throwsException() {
        String maliciousCode = """
            import java.io.File;
            public class FileHacker {
                public void deleteFiles() {
                    new File("/etc/passwd").delete();
                }
            }
            """;
        
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> CodeSecurityValidator.validate(maliciousCode));
        
        assertTrue(exception.getMessage().contains("Dangerous import detected"));
    }

    @Test
    void validate_maliciousNetworkCode_throwsException() {
        String maliciousCode = """
            import java.net.Socket;
            public class NetworkHacker {
                public void connectToServer() {
                    new Socket("evil.com", 80);
                }
            }
            """;
        
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> CodeSecurityValidator.validate(maliciousCode));
        
        assertTrue(exception.getMessage().contains("Dangerous import detected"));
    }

    @Test
    void validate_maliciousReflectionCode_throwsException() {
        String maliciousCode = """
            import java.lang.reflect.Method;
            public class ReflectionHacker {
                public void hackSystem() {
                    Class.forName("java.lang.System").getMethod("exit", int.class).invoke(null, 0);
                }
            }
            """;
        
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> CodeSecurityValidator.validate(maliciousCode));
        
        assertTrue(exception.getMessage().contains("Dangerous import detected"));
    }

    @Test
    void validate_nullCode_throwsException() {
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> CodeSecurityValidator.validate(null));
        
        assertTrue(exception.getMessage().contains("Source code cannot be null or empty"));
    }

    @Test
    void validate_emptyCode_throwsException() {
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> CodeSecurityValidator.validate(""));
        
        assertTrue(exception.getMessage().contains("Source code cannot be null or empty"));
    }

    @Test
    void validate_whitespaceCode_throwsException() {
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> CodeSecurityValidator.validate("   "));
        
        assertTrue(exception.getMessage().contains("Source code cannot be null or empty"));
    }
}
