# Usar uma imagem base do OpenJDK
FROM openjdk:24-jdk-slim

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o arquivo JAR da aplicação para dentro do container
COPY build/libs/Monolith-0.0.1-SNAPSHOT.jar my-app.jar

# Expor a porta do Spring Boot (por padrão 8080)
EXPOSE 8080

# Comando para rodar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "my-app.jar"]
