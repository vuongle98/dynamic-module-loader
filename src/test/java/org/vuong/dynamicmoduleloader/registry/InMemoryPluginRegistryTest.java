package org.vuong.dynamicmoduleloader.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vuong.dynamicmoduleloader.core.Plugin;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPluginRegistryTest {

    private InMemoryPluginRegistry registry;
    private Plugin testPlugin;

    @BeforeEach
    void setUp() throws Exception {
        registry = new InMemoryPluginRegistry();
        testPlugin = new Plugin("testPlugin", String.class);
    }

    @Test
    void register_storesPlugin() {
        Plugin result = registry.register(testPlugin);
        assertNull(result); // No previous plugin with same name
        
        Plugin retrieved = registry.getPlugin("testPlugin");
        assertSame(testPlugin, retrieved);
    }

    @Test
    void register_replacesExistingPlugin() {
        Plugin plugin1 = new Plugin("testPlugin", String.class);
        Plugin plugin2 = new Plugin("testPlugin", Integer.class);
        
        registry.register(plugin1);
        Plugin replaced = registry.register(plugin2);
        
        assertSame(plugin1, replaced);
        assertSame(plugin2, registry.getPlugin("testPlugin"));
    }

    @Test
    void register_throwsExceptionForNullPlugin() {
        assertThrows(IllegalArgumentException.class, () -> registry.register(null));
    }

    @Test
    void getPlugin_returnsNullForNonExistentPlugin() {
        assertNull(registry.getPlugin("nonExistent"));
    }

    @Test
    void getPlugin_throwsExceptionForNullOrEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> registry.getPlugin(null));
        assertThrows(IllegalArgumentException.class, () -> registry.getPlugin(""));
        assertThrows(IllegalArgumentException.class, () -> registry.getPlugin("   "));
    }

    @Test
    void removePlugin_removesAndReturnsPlugin() {
        registry.register(testPlugin);
        
        Plugin removed = registry.removePlugin("testPlugin");
        assertSame(testPlugin, removed);
        assertNull(registry.getPlugin("testPlugin"));
    }

    @Test
    void removePlugin_returnsNullForNonExistentPlugin() {
        assertNull(registry.removePlugin("nonExistent"));
    }

    @Test
    void removePlugin_throwsExceptionForNullOrEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> registry.removePlugin(null));
        assertThrows(IllegalArgumentException.class, () -> registry.removePlugin(""));
        assertThrows(IllegalArgumentException.class, () -> registry.removePlugin("   "));
    }

    @Test
    void getAllPlugins_returnsAllRegisteredPlugins() {
        Plugin plugin1 = new Plugin("plugin1", String.class);
        Plugin plugin2 = new Plugin("plugin2", Integer.class);
        
        registry.register(plugin1);
        registry.register(plugin2);
        
        Collection<Plugin> plugins = registry.getAllPlugins();
        assertEquals(2, plugins.size());
        assertTrue(plugins.contains(plugin1));
        assertTrue(plugins.contains(plugin2));
    }

    @Test
    void containsPlugin_returnsCorrectValue() {
        assertFalse(registry.containsPlugin("testPlugin"));
        
        registry.register(testPlugin);
        assertTrue(registry.containsPlugin("testPlugin"));
    }

    @Test
    void containsPlugin_throwsExceptionForNullOrEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> registry.containsPlugin(null));
        assertThrows(IllegalArgumentException.class, () -> registry.containsPlugin(""));
        assertThrows(IllegalArgumentException.class, () -> registry.containsPlugin("   "));
    }

    @Test
    void size_returnsCorrectCount() {
        assertEquals(0, registry.size());
        
        registry.register(testPlugin);
        assertEquals(1, registry.size());
        
        registry.register(new Plugin("another", String.class));
        assertEquals(2, registry.size());
    }

    @Test
    void clear_removesAllPlugins() {
        registry.register(testPlugin);
        registry.register(new Plugin("another", String.class));
        
        assertEquals(2, registry.size());
        
        registry.clear();
        
        assertEquals(0, registry.size());
        assertTrue(registry.getAllPlugins().isEmpty());
    }
}
