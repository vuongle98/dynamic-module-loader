import org.vuong.dynamicmoduleloader.PluginLoadService;
import org.vuong.dynamicmoduleloader.PluginRuntimeService;
import org.vuong.dynamicmoduleloader.core.Plugin;

/**
 * Simple usage guide for the Dynamic Module Loader.
 */
public class UsageGuide {
    
    public static void main(String[] args) {
        try {
            // Method 1: Load from existing Java file
            loadFromFile();
            
            // Method 2: Compile from string
            compileFromString();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example: Loading a plugin from a Java source file.
     */
    public static void loadFromFile() throws Exception {
        System.out.println("=== Loading Plugin from File ===");
        
        PluginLoadService loader = new PluginLoadService();
        
        // Method 1: Load and register the plugin
        Plugin plugin = loader.loadAndRegisterPlugin("example/TestPlugin.java");
        Class<?> clazz = plugin.getPluginClass();
        
        // Create an instance
        Object instance = clazz.getDeclaredConstructor().newInstance();
        
        // Call methods
        int result = (int) clazz.getMethod("sum", int.class, int.class).invoke(instance, 2, 3);
        String greeting = (String) clazz.getMethod("greet", String.class).invoke(instance, "User");
        double area = (double) clazz.getMethod("calculateArea", double.class).invoke(instance, 5.0);
        
        System.out.println("Sum: " + result);
        System.out.println("Greeting: " + greeting);
        System.out.println("Area of circle with radius 5: " + area);
        
        // Show registry functionality
        System.out.println("Plugin name: " + plugin.getName());
        System.out.println("Total plugins: " + loader.getPluginCount());
        System.out.println("Plugin exists: " + loader.containsPlugin("testPlugin"));
        
        // Method 2: Load without registering
        Plugin plugin2 = loader.loadPlugin("example/TestPlugin.java");
        System.out.println("Loaded without registration - Total plugins: " + loader.getPluginCount());
    }
    
    /**
     * Example: Compiling a plugin from source code string.
     */
    public static void compileFromString() throws Exception {
        System.out.println("\n=== Compiling Plugin from String ===");
        
        PluginRuntimeService service = new PluginRuntimeService();
        
        String className = "Calculator";
        String source = """
            public class Calculator {
                public int add(int a, int b) {
                    return a + b;
                }
                
                public int subtract(int a, int b) {
                    return a - b;
                }
            }
            """;
        
        // Compile and register the plugin
        Plugin plugin = service.compileAndRegister(className, source);
        Class<?> clazz = plugin.getPluginClass();
        Object instance = clazz.getDeclaredConstructor().newInstance();
        
        // Use the plugin
        int addResult = (int) clazz.getMethod("add", int.class, int.class).invoke(instance, 10, 5);
        int subtractResult = (int) clazz.getMethod("subtract", int.class, int.class).invoke(instance, 10, 3);
        
        System.out.println("Add result: " + addResult);
        System.out.println("Subtract result: " + subtractResult);
        System.out.println("Plugin name: " + plugin.getName());
    }
}
