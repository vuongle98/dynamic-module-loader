# Dynamic Module Loader (Pure Java Library)

A small Java library to compile and load Java classes at runtime, and manage them as simple "plugins" without any Spring dependency.

## Features

- **Runtime Compilation**: Compile Java source provided as a String into a class at runtime
- **File Loading**: Compile a `.java` file on disk and load it
- **Plugin Registry**: Maintain an in-memory registry of loaded plugins by name
- **Security Validation**: Comprehensive security checks to prevent malicious code execution
- **Configurable Security**: Flexible security policies for different use cases
- **Thread Safety**: Concurrent access support for multi-threaded environments
- **Easy Integration**: Published as a plain JAR (with sources/javadoc) for easy reuse

## Installation

Build and publish to your local Maven repository:

```bash
./gradlew clean build
./gradlew publishToMavenLocal
```

Add the dependency in your consumer project (Gradle):

```groovy
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation 'org.vuong:dynamic-module-loader:0.0.1-SNAPSHOT'
}
```

## Usage

### Compile source from a String

```java
import org.vuong.dynamicmoduleloader.PluginRuntimeService;
import org.vuong.dynamicmoduleloader.core.Plugin;

PluginRuntimeService service = new PluginRuntimeService();

String className = "MyPlugin";
String source = "public class MyPlugin { public int sum(int a,int b){ return a+b; } }";

Plugin plugin = service.compileAndRegister(className, source);
Class<?> clazz = plugin.getPluginClass();
Object instance = clazz.getDeclaredConstructor().newInstance();
int result = (int) clazz.getMethod("sum", int.class, int.class).invoke(instance, 2, 3);
// result == 5
```

### Load a plugin from a `.java` file

```java
import org.vuong.dynamicmoduleloader.PluginLoadService;
import org.vuong.dynamicmoduleloader.core.Plugin;

PluginLoadService loader = new PluginLoadService();

// Method 1: Load and register the plugin
Plugin plugin = loader.loadAndRegisterPlugin("/path/to/SomePlugin.java");
Class<?> clazz = plugin.getPluginClass();

// Method 2: Load without registering
Plugin plugin2 = loader.loadPlugin("/path/to/SomePlugin.java");
```

### Query the registry

```java
// Get a specific plugin
Plugin plugin = service.getPlugin("myPlugin");

// Check if a plugin exists
boolean exists = service.containsPlugin("myPlugin");

// Get all plugins
Collection<Plugin> allPlugins = service.getAllPlugins();

// Get plugin count
int count = service.getPluginCount();

// Remove a plugin
service.removePlugin("myPlugin");

// Clear all plugins
service.clearPlugins();
```

### Using custom registry implementations

```java
import org.vuong.dynamicmoduleloader.core.PluginRegistry;
import org.vuong.dynamicmoduleloader.registry.InMemoryPluginRegistry;

// Create services with custom registry
PluginRegistry customRegistry = new InMemoryPluginRegistry();
PluginRuntimeService runtimeService = new PluginRuntimeService(customRegistry);
PluginLoadService loadService = new PluginLoadService(customRegistry);
```

### PluginLoadService registry operations

```java
PluginLoadService loader = new PluginLoadService();

// Load and register plugins
Plugin plugin1 = loader.loadAndRegisterPlugin("/path/to/Plugin1.java");
Plugin plugin2 = loader.loadAndRegisterPlugin("/path/to/Plugin2.java");

// Registry operations
System.out.println("Total plugins: " + loader.getPluginCount());
System.out.println("Plugin exists: " + loader.containsPlugin("plugin1"));

// Get all plugins
Collection<Plugin> allPlugins = loader.getAllPlugins();

// Remove a plugin
loader.removePlugin("plugin1");

// Clear all plugins
loader.clearPlugins();
```

### Security Configuration

```java
import org.vuong.dynamicmoduleloader.security.SecurityConfig;

// Strict security (default) - blocks all dangerous operations
PluginRuntimeService strictService = new PluginRuntimeService();

// Moderate security - allows some operations
SecurityConfig moderateConfig = SecurityConfig.moderate();
PluginRuntimeService moderateService = new PluginRuntimeService(registry, moderateConfig);

// Permissive security - allows most operations (use with caution)
SecurityConfig permissiveConfig = SecurityConfig.permissive();
PluginRuntimeService permissiveService = new PluginRuntimeService(registry, permissiveConfig);
```

### Security Validation

The library automatically validates code for security threats:

```java
// This will be blocked by security validation
String maliciousCode = """
    public class MaliciousPlugin {
        public void executeCommand() {
            Runtime.getRuntime().exec("rm -rf /");
        }
    }
    """;

// This will throw SecurityException
service.compileAndRegister("MaliciousPlugin", maliciousCode);
```

**Blocked Operations:**
- File system access (`java.io.File`, `java.nio.file.*`)
- Network operations (`java.net.*`)
- System operations (`Runtime`, `ProcessBuilder`, `System.exit`)
- Reflection (`java.lang.reflect.*`)
- Database operations (`java.sql.*`)
- Dangerous method calls (`exec`, `invoke`, `forName`, etc.)

## Project Layout

The project follows Java library best practices with a simple, flat package structure:

### Core Package (`org.vuong.dynamicmoduleloader`)
- **`PluginRuntimeService`** - Main service for runtime plugin compilation and management
- **`PluginLoadService`** - Service for loading plugins from files

### Core Module (`core/`)
- **`Plugin`** - Core plugin entity
- **`PluginRegistry`** - Registry interface for plugin storage

### Compiler Module (`compiler/`)
- **`JavaSourceCompiler`** - Compiles Java source from strings
- **`JavaPluginCompiler`** - Compiles Java source files

### Registry Module (`registry/`)
- **`InMemoryPluginRegistry`** - In-memory plugin registry implementation

### Utility Module (`util/`)
- **`PluginNameUtils`** - Utility functions for plugin naming

### Test Coverage
- Comprehensive unit tests for all components
- Test coverage for utilities, services, and infrastructure

## Library Benefits

- **Simple Structure**: Easy to understand and use
- **Modular Design**: Clear separation of functionality
- **Extensibility**: Custom registry implementations supported
- **Thread Safety**: Concurrent access support
- **Comprehensive Testing**: Full test coverage

## Development

- Run tests:

```bash
./gradlew test
```

- Linting/formatting: not configured; open to adding Spotless or Checkstyle.

## Requirements

- JDK 17+

## License

MIT (or your preferred license).
