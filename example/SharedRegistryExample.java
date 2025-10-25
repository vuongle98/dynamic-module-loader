import org.vuong.dynamicmoduleloader.PluginLoadService;
import org.vuong.dynamicmoduleloader.PluginRuntimeService;
import org.vuong.dynamicmoduleloader.core.Plugin;
import org.vuong.dynamicmoduleloader.core.PluginRegistry;
import org.vuong.dynamicmoduleloader.registry.InMemoryPluginRegistry;

/**
 * Example demonstrating how to use a shared registry between services.
 */
public class SharedRegistryExample {
    
    public static void main(String[] args) {
        try {
            // Create a shared registry
            PluginRegistry sharedRegistry = new InMemoryPluginRegistry();
            
            System.out.println("=== Using Shared Registry ===");
            
            // Service 1: PluginRuntimeService
            PluginRuntimeService runtimeService = new PluginRuntimeService(sharedRegistry);
            
            String className = "MathPlugin";
            String source = """
                public class MathPlugin {
                    public int sum(int a, int b) {
                        return a + b;
                    }
                    
                    public int multiply(int a, int b) {
                        return a * b;
                    }
                }
                """;
            
            // Register plugin from runtime service
            Plugin plugin1 = runtimeService.compileAndRegister(className, source);
            Class<?> clazz1 = plugin1.getPluginClass();
            Object instance1 = clazz1.getDeclaredConstructor().newInstance();
            int sumResult = (int) clazz1.getMethod("sum", int.class, int.class).invoke(instance1, 5, 3);
            int multiplyResult = (int) clazz1.getMethod("multiply", int.class, int.class).invoke(instance1, 4, 6);
            
            System.out.println("MathPlugin - Sum: " + sumResult);
            System.out.println("MathPlugin - Multiply: " + multiplyResult);
            System.out.println("Plugin 1 name: " + plugin1.getName());
            
            // Service 2: PluginLoadService (using SAME registry)
            PluginLoadService loadService = new PluginLoadService(sharedRegistry);
            
            // Create a temporary plugin file for demonstration
            java.io.File tempFile = createTempPluginFile("StringPlugin", """
                public class StringPlugin {
                    public String greet(String name) {
                        return "Hello, " + name + "!";
                    }
                    
                    public String reverse(String text) {
                        return new StringBuilder(text).reverse().toString();
                    }
                }
                """);
            
            try {
                // Register plugin from load service
                Plugin plugin2 = loadService.loadAndRegisterPlugin(tempFile.getAbsolutePath());
                Class<?> clazz2 = plugin2.getPluginClass();
                Object instance2 = clazz2.getDeclaredConstructor().newInstance();
                String greeting = (String) clazz2.getMethod("greet", String.class).invoke(instance2, "World");
                String reversed = (String) clazz2.getMethod("reverse", String.class).invoke(instance2, "Hello");
                
                System.out.println("StringPlugin - Greeting: " + greeting);
                System.out.println("StringPlugin - Reversed: " + reversed);
                System.out.println("Plugin 2 name: " + plugin2.getName());
                
            } finally {
                tempFile.delete();
            }
            
            // Verify both plugins are in the shared registry
            System.out.println("\n=== Registry Status ===");
            System.out.println("Total plugins in shared registry: " + sharedRegistry.size());
            System.out.println("Plugin 'mathPlugin' exists: " + sharedRegistry.containsPlugin("mathPlugin"));
            System.out.println("Plugin 'stringPlugin' exists: " + sharedRegistry.containsPlugin("stringPlugin"));
            
            // List all plugins
            System.out.println("\nAll plugins in registry:");
            sharedRegistry.getAllPlugins().forEach(p -> 
                System.out.println("- " + p.getName() + " (" + p.getPluginClass().getSimpleName() + ")")
            );
            
            // Test cross-service access
            System.out.println("\n=== Cross-Service Access ===");
            Plugin retrievedFromRuntime = runtimeService.getPlugin("stringPlugin");
            Plugin retrievedFromLoad = loadService.getPlugin("mathPlugin");
            
            System.out.println("Runtime service can access StringPlugin: " + (retrievedFromRuntime != null));
            System.out.println("Load service can access MathPlugin: " + (retrievedFromLoad != null));
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static java.io.File createTempPluginFile(String className, String source) throws java.io.IOException {
        java.io.File tempDir = new java.io.File(System.getProperty("java.io.tmpdir"), "plugin-test-" + System.currentTimeMillis());
        tempDir.mkdirs();
        
        java.io.File tempFile = new java.io.File(tempDir, className + ".java");
        
        try (java.io.FileWriter writer = new java.io.FileWriter(tempFile)) {
            writer.write(source);
        }
        
        return tempFile;
    }
}
