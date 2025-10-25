package org.vuong.dynamicmoduleloader;

import org.junit.jupiter.api.Test;
import org.vuong.dynamicmoduleloader.core.Plugin;

import static org.junit.jupiter.api.Assertions.*;

class PluginRuntimeServiceTest {

    @Test
    void compileAndRegister_registersAndRetrievesPlugin() throws Exception {
        String className = "MyPlugin";
        String source = "public class MyPlugin { public int sum(int a,int b){ return a+b; } }";

        PluginRuntimeService service = new PluginRuntimeService();
        Plugin plugin = service.compileAndRegister(className, source);

        assertNotNull(plugin);
        assertEquals("myPlugin", plugin.getName());
        assertEquals(className, plugin.getPluginClass().getSimpleName());

        assertSame(plugin, service.getPlugin("myPlugin"));
        assertTrue(service.getAllPlugins().contains(plugin));
        assertTrue(service.containsPlugin("myPlugin"));
        assertEquals(1, service.getPluginCount());

        service.removePlugin("myPlugin");
        assertNull(service.getPlugin("myPlugin"));
        assertFalse(service.containsPlugin("myPlugin"));
        assertEquals(0, service.getPluginCount());
    }

    @Test
    void clearPlugins_removesAllPlugins() throws Exception {
        PluginRuntimeService service = new PluginRuntimeService();
        
        service.compileAndRegister("Plugin1", "public class Plugin1 {}");
        service.compileAndRegister("Plugin2", "public class Plugin2 {}");
        
        assertEquals(2, service.getPluginCount());
        
        service.clearPlugins();
        
        assertEquals(0, service.getPluginCount());
        assertTrue(service.getAllPlugins().isEmpty());
    }
}
