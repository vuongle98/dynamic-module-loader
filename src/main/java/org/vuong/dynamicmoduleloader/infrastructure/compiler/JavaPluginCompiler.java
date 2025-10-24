package org.vuong.dynamicmoduleloader.infrastructure.compiler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class JavaPluginCompiler {

    public Class<?> compileAndLoad(File javaFile) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, javaFile.getPath());

        if (result != 0) {
            throw new IllegalStateException("❌ Compilation failed for: " + javaFile.getName());
        }

        File dir = javaFile.getParentFile();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{dir.toURI().toURL()});
        String className = javaFile.getName().replace(".java", "");

        return Class.forName(className, true, classLoader);
    }
}
