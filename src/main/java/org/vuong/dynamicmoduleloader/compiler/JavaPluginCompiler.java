package org.vuong.dynamicmoduleloader.compiler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Compiler for Java source files on the filesystem.
 * 
 * This compiler takes Java source files from the filesystem and compiles them
 * into Class objects that can be loaded and instantiated. It uses the system
 * Java compiler and loads classes from the same directory as the source file.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public class JavaPluginCompiler {

    /**
     * Default constructor for JavaPluginCompiler.
     * Initializes the Java plugin compiler.
     */
    public JavaPluginCompiler() {
        // default constructor
    }

    /**
     * Compiles a Java source file and loads the resulting class.
     * 
     * This method compiles the specified Java file using the system Java compiler
     * and loads the resulting class using a URLClassLoader. The class name is
     * derived from the filename by removing the .java extension.
     * 
     * @param javaFile the Java source file to compile
     * @return the compiled Class object
     * @throws Exception if compilation fails or the class cannot be loaded
     * @throws IllegalArgumentException if javaFile is null or doesn't exist
     */
    public Class<?> compileAndLoad(File javaFile) throws Exception {
        if (javaFile == null) {
            throw new IllegalArgumentException("Java file cannot be null");
        }
        if (!javaFile.exists()) {
            throw new IllegalArgumentException("Java file does not exist: " + javaFile.getPath());
        }
        if (!javaFile.getName().endsWith(".java")) {
            throw new IllegalArgumentException("File must be a Java source file (.java): " + javaFile.getName());
        }
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("No system Java compiler. Ensure tests run on a JDK, not a JRE.");
        }
        
        int result = compiler.run(null, null, null, javaFile.getPath());

        if (result != 0) {
            throw new IllegalStateException("Compilation failed for: " + javaFile.getName());
        }

        File dir = javaFile.getParentFile();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{dir.toURI().toURL()});
        String className = javaFile.getName().replace(".java", "");

        return Class.forName(className, true, classLoader);
    }
}
