package org.vuong.dynamicmoduleloader.application.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.vuong.dynamicmoduleloader.domain.model.Plugin;
import org.vuong.dynamicmoduleloader.infrastructure.compiler.JavaPluginCompiler;

import java.io.File;

@Service
public class PluginLoadService {

    private final JavaPluginCompiler compiler = new JavaPluginCompiler(); // could inject if needed
    private final ConfigurableApplicationContext context;

    public PluginLoadService(ConfigurableApplicationContext context) {
        this.context = context;
    }

    public Plugin loadPlugin(String javaFilePath) throws Exception {
        File javaFile = new File(javaFilePath);
        Class<?> clazz = compiler.compileAndLoad(javaFile);
        Object instance = clazz.getDeclaredConstructor().newInstance();

        // Register as Spring Bean
        String beanName = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
        ConfigurableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton(beanName, instance);

        return new Plugin(beanName, clazz);
    }
}
