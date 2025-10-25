package org.vuong.dynamicmoduleloader;

import org.junit.jupiter.api.Test;
import org.vuong.dynamicmoduleloader.core.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PluginLoadServiceTest {

    @Test
    void loadAndRegisterPlugin_registersAndRetrievesPlugin() throws Exception {
        PluginLoadService service = new PluginLoadService();
        
        // Create a temporary Java file
        File tempFile = createTempPluginFile("TestPlugin", """
            public class TestPlugin {
                public int sum(int a, int b) {
                    return a + b;
                }
            }
            """);
        
        try {
            Plugin plugin = service.loadAndRegisterPlugin(tempFile.getAbsolutePath());
            
            assertNotNull(plugin);
            assertEquals("testPlugin", plugin.getName());
            assertEquals("TestPlugin", plugin.getPluginClass().getSimpleName());
            
            // Verify it's registered
            assertTrue(service.containsPlugin("testPlugin"));
            assertEquals(1, service.getPluginCount());
            
            // Retrieve from registry
            Plugin retrieved = service.getPlugin("testPlugin");
            assertSame(plugin, retrieved);
            
        } finally {
            tempFile.delete();
        }
    }

    @Test
    void loadPlugin_doesNotRegisterPlugin() throws Exception {
        PluginLoadService service = new PluginLoadService();
        
        File tempFile = createTempPluginFile("TestPlugin", """
            public class TestPlugin {
                public int sum(int a, int b) {
                    return a + b;
                }
            }
            """);
        
        try {
            Plugin plugin = service.loadPlugin(tempFile.getAbsolutePath());
            
            assertNotNull(plugin);
            assertEquals("testPlugin", plugin.getName());
            
            // Verify it's NOT registered
            assertFalse(service.containsPlugin("testPlugin"));
            assertEquals(0, service.getPluginCount());
            
        } finally {
            tempFile.delete();
        }
    }

    @Test
    void registryOperations_workCorrectly() throws Exception {
        PluginLoadService service = new PluginLoadService();
        
        File tempFile1 = createTempPluginFile("Plugin1", "public class Plugin1 {}");
        File tempFile2 = createTempPluginFile("Plugin2", "public class Plugin2 {}");
        
        try {
            // Load and register two plugins
            Plugin plugin1 = service.loadAndRegisterPlugin(tempFile1.getAbsolutePath());
            Plugin plugin2 = service.loadAndRegisterPlugin(tempFile2.getAbsolutePath());
            
            assertEquals(2, service.getPluginCount());
            assertTrue(service.containsPlugin("plugin1"));
            assertTrue(service.containsPlugin("plugin2"));
            
            // Test getAllPlugins
            assertEquals(2, service.getAllPlugins().size());
            assertTrue(service.getAllPlugins().contains(plugin1));
            assertTrue(service.getAllPlugins().contains(plugin2));
            
            // Remove one plugin
            Plugin removed = service.removePlugin("plugin1");
            assertSame(plugin1, removed);
            assertEquals(1, service.getPluginCount());
            assertFalse(service.containsPlugin("plugin1"));
            
            // Clear all plugins
            service.clearPlugins();
            assertEquals(0, service.getPluginCount());
            assertTrue(service.getAllPlugins().isEmpty());
            
        } finally {
            tempFile1.delete();
            tempFile2.delete();
        }
    }

    @Test
    void constructor_withCustomRegistry() {
        PluginLoadService service1 = new PluginLoadService();
        PluginLoadService service2 = new PluginLoadService();
        
        // They should have separate registries
        assertNotSame(service1.getAllPlugins(), service2.getAllPlugins());
    }

    @Test
    void constructor_throwsExceptionForNullRegistry() {
        assertThrows(IllegalArgumentException.class, () -> new PluginLoadService(null));
    }

    private File createTempPluginFile(String className, String source) throws IOException {
        // Create a temporary directory first
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "plugin-test-" + System.currentTimeMillis());
        tempDir.mkdirs();
        
        // Create the Java file with the correct name
        File tempFile = new File(tempDir, className + ".java");
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(source);
        }
        
        return tempFile;
    }
}
