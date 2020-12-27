# API Signature Check jlink Plug-in

A proof-of-concept for a plug-in for the jlink tool which detects differences between the API exposed by a module added to a custom Java runtime image and other modules calling this API.
This helps to prevent ``NoSuchMethodError``s at application runtime,
caused by using different versions of a module at compile time vs. runtime.

Note: The jlink plug-in API is currently not officially supported nor exposed by the _jdk.jlink_ module.
Hence [some trickery](https://in.relation.to/2017/12/12/exploring-jlink-plugin-api-in-java-9/#trick-2-the-java-agent) is required for compiling and using this plug-in.

## Usage

Build the project as described below. Then create a custom runtime image for the _example_ modules:

```shell
$JAVA_HOME/bin/jlink \
  -J-javaagent:agent/target/signature-check-jlink-plugin-registration-agent-1.0-SNAPSHOT.jar \
  -J--module-path=plugin/target/signature-check-jlink-plugin-1.0-SNAPSHOT.jar:/Users/gunnar/.m2/repository/org/codehaus/mojo/animal-sniffer/1.19/animal-sniffer-1.19.jar:/Users/gunnar/.m2/repository/org/ow2/asm/asm/9.0/asm-9.0.jar \
  -J--add-modules=dev.morling.jlink.plugins.sigcheck \
  --module-path=$JAVA_HOME/jmods/:example/customer-2/target/customer-2-1.0-SNAPSHOT.jar:example/order/target/order-1.0-SNAPSHOT.jar \
  --add-modules=com.example.order \
  --output=target/runtime-image \
  --check-signatures
```

The `order` module is compiled against the `customer-1` module.
The runtime image is created using the `customer-2` module though, which contains a breaking API change.
When not using this plug-in, this would result in a `NoSuchMethodError` at runtime.
With this plug-in, the mismatching API signature will be detected at link time and raised as an error.

## Build

This project requires OpenJDK 14 or later for its build.
Apache Maven is used for the build.
Run the following to build the project:

```shell
mvn clean install
```

## License

This code base is available under the Apache License, version 2.