#!/bin/bash
set -e

# Define JavaFX SDK path
JAVAFX_LIB="lib/javafx-sdk-21.0.1/lib"

# Clean output directory
rm -rf out
mkdir -p out/classes
mkdir -p out/build/temp_libs

# Compile
echo "Compiling..."
javac --module-path $JAVAFX_LIB --add-modules javafx.controls,javafx.fxml,javafx.graphics \
    -d out/classes \
    src/main/java/com/grades/*.java

# Prepare Fat Jar content
echo "Preparing Fat JAR..."

# 1. Extract JavaFX JARs
for jar in $JAVAFX_LIB/*.jar; do
    unzip -q -o "$jar" -d out/build/temp_libs
done

# 2. Delete module-info.class to avoid module conflicts
rm -rf out/build/temp_libs/module-info.class

# 3. Copy compiled application classes
cp -r out/classes/* out/build/temp_libs/

# 4. Copy resources
cp src/main/resources/style.css out/build/temp_libs/

# 5. Copy native libraries (Linux *.so) to root of JAR (experimental, ensuring they are packaged)
cp $JAVAFX_LIB/*.so out/build/temp_libs/

# Create JAR
echo "Creating JAR..."
jar --create --file out/build/nota_calc.jar --main-class com.grades.Launcher -C out/build/temp_libs .

# Cleanup temp
rm -rf out/build/temp_libs

# Run
echo "Running..."
echo "To run manually: java -jar out/build/nota_calc.jar"
echo "---------------------------------------------------"

# Run the generated JAR
java -jar out/build/nota_calc.jar
