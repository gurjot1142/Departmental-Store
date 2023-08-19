FROM openjdk:11

COPY target/DepartmentalStore-0.1.0.jar DepartmentalStore-0.1.0.jar

ENTRYPOINT ["java", "-jar", "/DepartmentalStore-0.1.0.jar"]