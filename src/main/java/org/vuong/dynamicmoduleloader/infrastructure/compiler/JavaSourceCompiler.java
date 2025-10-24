package org.vuong.dynamicmoduleloader.infrastructure.compiler;

import javax.tools.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class JavaSourceCompiler {

    public Class<?> compileFromText(String className, String javaSource) throws Exception {
        Path sourceFile = Files.createTempFile(className, ".java");
        Files.write(sourceFile, javaSource.getBytes());

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, sourceFile.toString());

        if (result != 0) {
            throw new IllegalStateException("Compilation failed");
        }

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{sourceFile.getParent().toUri().toURL()});
        return Class.forName(className, true, classLoader);
    }
}
