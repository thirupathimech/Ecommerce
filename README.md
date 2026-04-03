# Ecommerce - Docker Deployment (Payara 6.2025.10)

This project can be deployed with **Docker + Docker Compose** using:
- **Payara Server Full 6.2025.10 (JDK 17)** for app server
- **MySQL 8.4** for database

## 1) Build and run

```bash
docker compose up --build -d
```

## 2) Access URLs

- Application: `http://localhost:8080/ecommerce/`
- Payara Admin Console: `http://localhost:4848`

## 3) Database configuration (important)

Your application uses JPA with this JNDI datasource in `persistence.xml`:
- `jdbc/ecomDS`

At container startup, `docker/init-payara.sh` automatically:
1. Starts Payara domain
2. Creates JDBC Connection Pool (`ecomPool` by default)
3. Creates JDBC Resource (`jdbc/ecomDS` by default)
4. Pings pool and starts Payara in foreground

So in server-level database config, you only need to set environment variables.

### Supported environment variables

| Variable | Default | Purpose |
|---|---|---|
| `DB_HOST` | `mysql` | MySQL hostname/service name |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `ecommerce` | Database name |
| `DB_USER` | `ecom_user` | Database user |
| `DB_PASSWORD` | `ecom_password` | Database password |
| `DB_POOL_NAME` | `ecomPool` | Payara JDBC pool name |
| `DB_JNDI_NAME` | `jdbc/ecomDS` | JNDI datasource name used by app |

## 4) Stop containers

```bash
docker compose down
```

To remove DB volume too:

```bash
docker compose down -v
```

## 5) Notes for production server

- Use strong DB credentials (do not keep defaults).
- Keep `DB_JNDI_NAME=jdbc/ecomDS` unless you also change `src/main/resources/META-INF/persistence.xml`.
- Open only required ports in firewall (usually 8080; 4848 only for admin access).
