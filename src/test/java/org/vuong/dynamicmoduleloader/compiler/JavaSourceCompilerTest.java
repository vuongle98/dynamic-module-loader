package org.vuong.dynamicmoduleloader.compiler;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class JavaSourceCompilerTest {

    @Test
    void compileFromText_compilesAndLoadsClass() throws Exception {
        String className = "Hello";
        String source = "public class Hello { public String hi(){ return \"ok\"; } }";

        JavaSourceCompiler compiler = new JavaSourceCompiler();
        Class<?> clazz = compiler.compileFromText(className, source);
        assertNotNull(clazz, "Compiled class should not be null");
        assertEquals(className, clazz.getSimpleName());

        Object instance = clazz.getDeclaredConstructor().newInstance();
        Method m = clazz.getMethod("hi");
        Object result = m.invoke(instance);
        assertEquals("ok", result);
    }
}
