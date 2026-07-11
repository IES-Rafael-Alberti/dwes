# 🧱 Compila versión JVM tradicional
build-jvm:
    ./gradlew clean build -x test

# 🧱 Compila imagen nativa con GraalVM
build-native:
    ./gradlew clean nativeCompile -x test

# 🐳 Construye imagen Docker JVM
docker-jvm:
    docker build -t kotlin-jvm -f Dockerfile.jvm .

# 🐳 Construye imagen Docker nativa
docker-native:
    docker build -t kotlin-native -f Dockerfile.native .

# 🚀 Levanta ambos contenedores
run:
    docker-compose up

# 🧹 Limpia todo
clean:
    ./gradlew clean
    docker-compose down
    docker image rm kotlin-jvm kotlin-native || true