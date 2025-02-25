# Используем образ Maven с OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходники и собираем проект
COPY src ./src
RUN mvn package -DskipTests

# Используем минимальный образ JDK для запуска
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Копируем собранный JAR-файл
COPY --from=build /app/target/*.jar app.jar

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]
