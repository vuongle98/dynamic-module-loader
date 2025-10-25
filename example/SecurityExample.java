import org.vuong.dynamicmoduleloader.PluginLoadService;
import org.vuong.dynamicmoduleloader.PluginRuntimeService;
import org.vuong.dynamicmoduleloader.core.Plugin;
import org.vuong.dynamicmoduleloader.core.PluginRegistry;
import org.vuong.dynamicmoduleloader.registry.InMemoryPluginRegistry;
import org.vuong.dynamicmoduleloader.security.SecurityConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Example demonstrating security validation features.
 */
public class SecurityExample {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Security Validation Examples ===\n");
            
            // Example 1: Safe code (should work)
            testSafeCode();
            
            // Example 2: Malicious code (should be blocked)
            testMaliciousCode();
            
            // Example 3: Different security configurations
            testSecurityConfigurations();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test safe code that should pass security validation.
     */
    private static void testSafeCode() {
        System.out.println("1. Testing Safe Code:");
        
        try {
            PluginRuntimeService service = new PluginRuntimeService();
            
            String safeCode = """
                public class SafeCalculator {
                    public int add(int a, int b) {
                        return a + b;
                    }
                    
                    public int subtract(int a, int b) {
                        return a - b;
                    }
                    
                    public String greet(String name) {
                        return "Hello, " + name + "!";
                    }
                }
                """;
            
            Plugin plugin = service.compileAndRegister("SafeCalculator", safeCode);
            Class<?> clazz = plugin.getPluginClass();
            Object instance = clazz.getDeclaredConstructor().newInstance();
            
            int result = (int) clazz.getMethod("add", int.class, int.class).invoke(instance, 5, 3);
            String greeting = (String) clazz.getMethod("greet", String.class).invoke(instance, "World");
            
            System.out.println("✅ Safe code executed successfully!");
            System.out.println("   Add result: " + result);
            System.out.println("   Greeting: " + greeting);
            
        } catch (Exception e) {
            System.out.println("❌ Unexpected error with safe code: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Test malicious code that should be blocked by security validation.
     */
    private static void testMaliciousCode() {
        System.out.println("2. Testing Malicious Code (should be blocked):");
        
        // Test 1: Runtime execution
        testMaliciousRuntimeCode();
        
        // Test 2: File operations
        testMaliciousFileOperations();
        
        // Test 3: Network operations
        testMaliciousNetworkCode();
        
        // Test 4: Reflection
        testMaliciousReflectionCode();
        
        System.out.println();
    }
    
    private static void testMaliciousRuntimeCode() {
        try {
            PluginRuntimeService service = new PluginRuntimeService();
            
            String maliciousCode = """
                public class MaliciousPlugin {
                    public void executeCommand() {
                        Runtime.getRuntime().exec("rm -rf /");
                    }
                }
                """;
            
            service.compileAndRegister("MaliciousPlugin", maliciousCode);
            System.out.println("❌ Security validation failed - malicious code was allowed!");
            
        } catch (Exception e) {
            System.out.println("✅ Malicious runtime code blocked: " + e.getMessage());
        }
    }
    
    private static void testMaliciousFileOperations() {
        try {
            PluginRuntimeService service = new PluginRuntimeService();
            
            String maliciousCode = """
                import java.io.File;
                public class FileHacker {
                    public void deleteFiles() {
                        new File("/etc/passwd").delete();
                    }
                }
                """;
            
            service.compileAndRegister("FileHacker", maliciousCode);
            System.out.println("❌ Security validation failed - file operations allowed!");
            
        } catch (Exception e) {
            System.out.println("✅ Malicious file operations blocked: " + e.getMessage());
        }
    }
    
    private static void testMaliciousNetworkCode() {
        try {
            PluginRuntimeService service = new PluginRuntimeService();
            
            String maliciousCode = """
                import java.net.Socket;
                public class NetworkHacker {
                    public void connectToServer() {
                        new Socket("evil.com", 80);
                    }
                }
                """;
            
            service.compileAndRegister("NetworkHacker", maliciousCode);
            System.out.println("❌ Security validation failed - network operations allowed!");
            
        } catch (Exception e) {
            System.out.println("✅ Malicious network code blocked: " + e.getMessage());
        }
    }
    
    private static void testMaliciousReflectionCode() {
        try {
            PluginRuntimeService service = new PluginRuntimeService();
            
            String maliciousCode = """
                import java.lang.reflect.Method;
                public class ReflectionHacker {
                    public void hackSystem() {
                        Class.forName("java.lang.System").getMethod("exit", int.class).invoke(null, 0);
                    }
                }
                """;
            
            service.compileAndRegister("ReflectionHacker", maliciousCode);
            System.out.println("❌ Security validation failed - reflection allowed!");
            
        } catch (Exception e) {
            System.out.println("✅ Malicious reflection code blocked: " + e.getMessage());
        }
    }
    
    /**
     * Test different security configurations.
     */
    private static void testSecurityConfigurations() {
        System.out.println("3. Testing Different Security Configurations:");
        
        // Test strict security (default)
        testStrictSecurity();
        
        // Test moderate security
        testModerateSecurity();
        
        // Test permissive security
        testPermissiveSecurity();
    }
    
    private static void testStrictSecurity() {
        try {
            PluginRuntimeService service = new PluginRuntimeService();
            
            String codeWithFileImport = """
                import java.io.File;
                public class FileUser {
                    public void useFile() {
                        new File("test.txt");
                    }
                }
                """;
            
            service.compileAndRegister("FileUser", codeWithFileImport);
            System.out.println("❌ Strict security failed - file operations allowed!");
            
        } catch (Exception e) {
            System.out.println("✅ Strict security working: " + e.getMessage());
        }
    }
    
    private static void testModerateSecurity() {
        try {
            SecurityConfig moderateConfig = SecurityConfig.moderate();
            PluginRuntimeService service = new PluginRuntimeService(new InMemoryPluginRegistry(), moderateConfig);
            
            String safeCode = """
                public class ModeratePlugin {
                    public int calculate(int x) {
                        return x * 2;
                    }
                }
                """;
            
            Plugin plugin = service.compileAndRegister("ModeratePlugin", safeCode);
            System.out.println("✅ Moderate security allows safe code");
            
        } catch (Exception e) {
            System.out.println("❌ Moderate security failed: " + e.getMessage());
        }
    }
    
    private static void testPermissiveSecurity() {
        try {
            SecurityConfig permissiveConfig = SecurityConfig.permissive();
            PluginRuntimeService service = new PluginRuntimeService(new InMemoryPluginRegistry(), permissiveConfig);
            
            String codeWithFileImport = """
                import java.io.File;
                public class PermissiveFileUser {
                    public void useFile() {
                        new File("test.txt");
                    }
                }
                """;
            
            Plugin plugin = service.compileAndRegister("PermissiveFileUser", codeWithFileImport);
            System.out.println("✅ Permissive security allows file operations");
            
        } catch (Exception e) {
            System.out.println("❌ Permissive security failed: " + e.getMessage());
        }
    }
}
