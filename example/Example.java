import org.vuong.dynamicmoduleloader.PluginLoadService;
import org.vuong.dynamicmoduleloader.PluginRuntimeService;
import org.vuong.dynamicmoduleloader.core.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Example demonstrating how to use the Dynamic Module Loader library.
 */
public class Example {
    
    public static void main(String[] args) {
        try {
            // Example 1: Using PluginRuntimeService (compile from string)
            System.out.println("=== Example 1: PluginRuntimeService ===");
            PluginRuntimeService runtimeService = new PluginRuntimeService();
            
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
            
            Plugin plugin1 = runtimeService.compileAndRegister(className, source);
            Class<?> clazz1 = plugin1.getPluginClass();
            Object instance1 = clazz1.getDeclaredConstructor().newInstance();
            
            int sumResult = (int) clazz1.getMethod("sum", int.class, int.class).invoke(instance1, 5, 3);
            int multiplyResult = (int) clazz1.getMethod("multiply", int.class, int.class).invoke(instance1, 4, 6);
            
            System.out.println("Sum result: " + sumResult);
            System.out.println("Multiply result: " + multiplyResult);
            System.out.println("Plugin name: " + plugin1.getName());
            System.out.println("Total plugins: " + runtimeService.getPluginCount());
            
            // Example 2: Using PluginLoadService (load from file)
            System.out.println("\n=== Example 2: PluginLoadService ===");
            PluginLoadService loadService = new PluginLoadService();
            
            // Create a temporary Java file for demonstration
            File tempFile = createTempPluginFile();
            
            try {
                Plugin plugin2 = loadService.loadPlugin(tempFile.getAbsolutePath());
                Class<?> clazz2 = plugin2.getPluginClass();
                Object instance2 = clazz2.getDeclaredConstructor().newInstance();
                
                int result2 = (int) clazz2.getMethod("sum", int.class, int.class).invoke(instance2, 10, 20);
                String greeting = (String) clazz2.getMethod("greet", String.class).invoke(instance2, "World");
                
                System.out.println("Sum result: " + result2);
                System.out.println("Greeting: " + greeting);
                System.out.println("Plugin name: " + plugin2.getName());
                
            } finally {
                // Clean up temporary file
                tempFile.delete();
            }
            
            // Example 3: Registry operations
            System.out.println("\n=== Example 3: Registry Operations ===");
            System.out.println("All plugins in registry:");
            runtimeService.getAllPlugins().forEach(p -> 
                System.out.println("- " + p.getName() + " (" + p.getPluginClass().getSimpleName() + ")")
            );
            
            // Check if plugin exists
            boolean exists = runtimeService.containsPlugin("mathPlugin");
            System.out.println("Plugin 'mathPlugin' exists: " + exists);
            
            // Remove plugin
            runtimeService.removePlugin("mathPlugin");
            System.out.println("After removal - Total plugins: " + runtimeService.getPluginCount());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a temporary Java plugin file for demonstration.
     */
    private static File createTempPluginFile() throws IOException {
        File tempFile = File.createTempFile("TestPlugin", ".java");
        
        String pluginSource = """
            public class TestPlugin {
                public int sum(int a, int b) {
                    return a + b;
                }
                
                public String greet(String name) {
                    return "Hello, " + name + "!";
                }
            }
            """;
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(pluginSource);
        }
        
        return tempFile;
    }
}
