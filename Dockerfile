# -------- Build stage --------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

# -------- Runtime stage --------
FROM payara/server-full:6.2025.10-jdk17

USER root

ENV PAYARA_HOME=/opt/payara/appserver \
    DOMAIN=domain1

# Copy WAR built in previous stage
COPY --from=build /build/target/ecommerce-1.0-SNAPSHOT.war ${PAYARA_HOME}/glassfish/domains/${DOMAIN}/autodeploy/ecommerce.war

# Copy and prepare startup script
COPY docker/init-payara.sh /opt/payara/init-payara.sh
RUN chmod +x /opt/payara/init-payara.sh

ENV DB_HOST=mysql \
    DB_PORT=3306 \
    DB_NAME=ecommerce \
    DB_USER=ecom_user \
    DB_PASSWORD=ecom_password \
    DB_POOL_NAME=ecomPool \
    DB_JNDI_NAME=jdbc/ecomDS

EXPOSE 8080 4848

CMD ["/opt/payara/init-payara.sh"]
