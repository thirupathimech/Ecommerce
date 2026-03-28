# -------- Build stage --------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

# -------- Runtime stage --------
FROM eclipse/glassfish:7.0.0

USER root

# Copy WAR built in previous stage
COPY --from=build /build/target/ecommerce-1.0-SNAPSHOT.war /opt/glassfish7/glassfish/domains/domain1/autodeploy/ecommerce.war

# Copy and prepare startup script
COPY docker/init-glassfish.sh /opt/glassfish7/init-glassfish.sh
RUN chmod +x /opt/glassfish7/init-glassfish.sh

ENV DB_HOST=mysql \
    DB_PORT=3306 \
    DB_NAME=ecommerce \
    DB_USER=ecom_user \
    DB_PASSWORD=ecom_password \
    DB_POOL_NAME=ecomPool \
    DB_JNDI_NAME=jdbc/ecomDS

EXPOSE 8080 4848

CMD ["/opt/glassfish7/init-glassfish.sh"]
