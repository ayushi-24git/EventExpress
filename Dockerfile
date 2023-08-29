# Using official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Setting working directory in the container
WORKDIR /app

# Copying compiled Java class files into the container
COPY build/classes/java/main/Event.class /app
COPY build/classes/java/main/EventProcessor.class /app

# Copying jar files into the container
COPY src/lib/commons-pool2-2.11.1.jar /app
COPY src/lib/commons-pool2-2.4.2.jar /app
COPY src/lib/jedis-2.8.1.jar /app

# Running the application
CMD ["java", "-cp", "commons-pool2-2.11.1.jar:commons-pool2-2.4.2.jar:jedis-2.8.1.jar:.", "EventProcessor"]
