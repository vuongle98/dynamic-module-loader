package org.vuong.dynamicmoduleloader.compiler;

import javax.tools.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/**
 * Compiler for Java source code provided as strings.
 *
 * This compiler takes Java source code as a string and compiles it into
 * a Class object that can be loaded and instantiated. It uses the system
 * Java compiler and creates temporary files for compilation.
 * 
 * @author Dynamic Module Loader
 * @since 0.0.1-SNAPSHOT
 */
public class JavaSourceCompiler {

    /**
     * Default constructor for JavaSourceCompiler.
     * Initializes the Java source compiler.
     */
    public JavaSourceCompiler() {
        // default constructor
    }

    /**
     * Compiles Java source code from a string into a Class object.
     * 
     * This method creates a temporary directory, writes the source code to a
     * temporary .java file, compiles it using the system Java compiler, and
     * loads the resulting class using a URLClassLoader.
     * 
     * @param className the name of the class to compile
     * @param javaSource the Java source code as a string
     * @return the compiled Class object
     * @throws Exception if compilation fails or the class cannot be loaded
     * @throws IllegalArgumentException if className or javaSource is null or empty
     */
    public Class<?> compileFromText(String className, String javaSource) throws Exception {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be null or empty");
        }
        if (javaSource == null || javaSource.trim().isEmpty()) {
            throw new IllegalArgumentException("Java source cannot be null or empty");
        }
        
        Path tempDir = Files.createTempDirectory("dyn-compiler-");
        try {
            Path sourceFile = tempDir.resolve(className + ".java");
            Files.write(sourceFile, javaSource.getBytes());

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new IllegalStateException("No system Java compiler. Ensure tests run on a JDK, not a JRE.");
            }

            int result = compiler.run(null, null, null, sourceFile.toString());
            if (result != 0) {
                throw new IllegalStateException("Compilation failed for " + sourceFile.getFileName());
            }

            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{tempDir.toUri().toURL()});
            return Class.forName(className, true, classLoader);
        } finally {
            // Clean up temporary directory
            try {
                Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // Log warning but don't fail
                            System.err.println("Warning: Could not delete temporary file: " + path);
                        }
                    });
            } catch (IOException e) {
                System.err.println("Warning: Could not clean up temporary directory: " + tempDir);
            }
        }
    }
}
