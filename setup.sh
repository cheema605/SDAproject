#!/bin/bash
# Setup script to configure Maven wrapper environment for Linux/Mac

echo "Detecting Java installation..."

# Try common Java locations
if [ -d "/usr/lib/jvm/java-25-eclipse-adoptium" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-25-eclipse-adoptium"
    echo "Found Java at: $JAVA_HOME"
elif [ -d "/usr/local/opt/openjdk@25" ]; then
    export JAVA_HOME="/usr/local/opt/openjdk@25"
    echo "Found Java at: $JAVA_HOME"
elif [ -d "/opt/java/openjdk" ]; then
    export JAVA_HOME="/opt/java/openjdk"
    echo "Found Java at: $JAVA_HOME"
elif [ -n "$JAVA_HOME" ] && [ -d "$JAVA_HOME" ]; then
    echo "JAVA_HOME already set to: $JAVA_HOME"
else
    echo "Error: Could not find Java installation"
    echo "Please set JAVA_HOME environment variable manually"
    exit 1
fi

echo ""
echo "Maven wrapper environment configured successfully"
echo "You can now run Maven commands:"
echo "   ./mvnw compile"
echo "   ./mvnw test"
echo "   ./mvnw javafx:run"
