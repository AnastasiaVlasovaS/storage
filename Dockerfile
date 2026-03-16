# Используем официальный образ OpenJDK 19
FROM openjdk:19-jdk-slim

# Тут указываем рабочую папку внутри контейнера
WORKDIR /app

# Копируем собранный jar-файл сюда
COPY target/имя_вашего_файла.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]