LIB_DIR="lib"

BIN_DIR="bin"

SRC_DIR="src"

MAIN_CLASS="ResumeScannerApp"

CP="$LIB_DIR/mysql-connector-java-8.0.28.jar:$LIB_DIR/pdfbox-app-2.0.27.jar"

echo "Cleaning old build files..."
rm -rf $BIN_DIR/*

echo "Compiling source files..."

javac -d $BIN_DIR -cp "$CP" $(find $SRC_DIR -name "*.java")

if [ $? -ne 0 ]; then
  echo "ERROR: Compilation failed."
  exit 1
fi

echo "Compilation successful."

echo "Running the application..."

java -cp "$BIN_DIR:$CP" $MAIN_CLASS
