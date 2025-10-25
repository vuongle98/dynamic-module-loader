package org.vuong.dynamicmoduleloader.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PluginNameUtilsTest {

    @Test
    void generatePluginName_convertsFirstCharacterToLowercase() {
        assertEquals("myPlugin", PluginNameUtils.generatePluginName("MyPlugin"));
        assertEquals("plugin", PluginNameUtils.generatePluginName("Plugin"));
        assertEquals("a", PluginNameUtils.generatePluginName("A"));
    }

    @Test
    void generatePluginName_handlesSingleCharacter() {
        assertEquals("a", PluginNameUtils.generatePluginName("A"));
        assertEquals("b", PluginNameUtils.generatePluginName("b"));
    }

    @Test
    void generatePluginName_throwsExceptionForNullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> PluginNameUtils.generatePluginName(null));
        assertThrows(IllegalArgumentException.class, () -> PluginNameUtils.generatePluginName(""));
        assertThrows(IllegalArgumentException.class, () -> PluginNameUtils.generatePluginName("   "));
    }

    @Test
    void isValidPluginName_validatesCorrectly() {
        assertTrue(PluginNameUtils.isValidPluginName("myPlugin"));
        assertTrue(PluginNameUtils.isValidPluginName("plugin"));
        assertTrue(PluginNameUtils.isValidPluginName("_plugin"));
        assertTrue(PluginNameUtils.isValidPluginName("plugin123"));
        assertTrue(PluginNameUtils.isValidPluginName("plugin_name"));
        
        assertFalse(PluginNameUtils.isValidPluginName(null));
        assertFalse(PluginNameUtils.isValidPluginName(""));
        assertFalse(PluginNameUtils.isValidPluginName("   "));
        assertFalse(PluginNameUtils.isValidPluginName(" myPlugin"));
        assertFalse(PluginNameUtils.isValidPluginName("myPlugin "));
        assertFalse(PluginNameUtils.isValidPluginName("123plugin"));
        assertFalse(PluginNameUtils.isValidPluginName("plugin-name"));
        assertFalse(PluginNameUtils.isValidPluginName("plugin.name"));
    }
}
