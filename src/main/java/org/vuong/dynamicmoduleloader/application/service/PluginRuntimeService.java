package org.vuong.dynamicmoduleloader.application.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.vuong.dynamicmoduleloader.domain.model.Plugin;
import org.vuong.dynamicmoduleloader.infrastructure.compiler.JavaSourceCompiler;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PluginRuntimeService {

    private final JavaSourceCompiler compiler = new JavaSourceCompiler();
    private final ConfigurableApplicationContext context;
    private final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();

    public PluginRuntimeService(ConfigurableApplicationContext context) {
        this.context = context;
    }

    public Plugin compileAndRegister(String className, String javaCode) throws Exception {
        Class<?> clazz = compiler.compileFromText(className, javaCode);
        Object instance = clazz.getDeclaredConstructor().newInstance();

        String beanName = Character.toLowerCase(className.charAt(0)) + className.substring(1);
        context.getBeanFactory().registerSingleton(beanName, instance);

        Plugin plugin = new Plugin(beanName, clazz);
        plugins.put(beanName, plugin);
        return plugin;
    }

    public Plugin getPlugin(String name) {
        return plugins.get(name);
    }

    public void removePlugin(String name) {
        plugins.remove(name);
        // Remove bean (not officially supported in Spring, workarounds possible)
    }

    public Collection<Plugin> getAllPlugins() {
        return plugins.values();
    }
}
