#!/usr/bin/env bash
set -euo pipefail

ASADMIN="/opt/glassfish7/bin/asadmin"
DOMAIN="domain1"

# Start domain in background to run admin commands
$ASADMIN start-domain "$DOMAIN"

# Ensure MySQL JDBC driver is available at domain level
mkdir -p /opt/glassfish7/glassfish/domains/$DOMAIN/lib
if [ -f /opt/glassfish7/glassfish/modules/mysql-connector-j.jar ]; then
  cp /opt/glassfish7/glassfish/modules/mysql-connector-j.jar /opt/glassfish7/glassfish/domains/$DOMAIN/lib/mysql-connector-j.jar || true
fi

# Create JDBC connection pool if missing
if ! $ASADMIN list-jdbc-connection-pools | grep -q "${DB_POOL_NAME}"; then
  $ASADMIN create-jdbc-connection-pool \
    --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
    --restype javax.sql.DataSource \
    --property user=${DB_USER}:password=${DB_PASSWORD}:ServerName=${DB_HOST}:PortNumber=${DB_PORT}:DatabaseName=${DB_NAME}:useSSL=false:allowPublicKeyRetrieval=true \
    "${DB_POOL_NAME}"
fi

# Create JNDI resource if missing
if ! $ASADMIN list-jdbc-resources | grep -q "${DB_JNDI_NAME}"; then
  $ASADMIN create-jdbc-resource --connectionpoolid "${DB_POOL_NAME}" "${DB_JNDI_NAME}"
fi

# Validate pool
$ASADMIN ping-connection-pool "${DB_POOL_NAME}" || true

# Stop and restart in foreground
$ASADMIN stop-domain "$DOMAIN"
exec $ASADMIN start-domain --verbose "$DOMAIN"
