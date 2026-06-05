# LINHS Portal Deployment Guide

## Quick Start Checklist

- [ ] Review FIX_SUMMARY.md for all fixes applied
- [ ] Set up PostgreSQL database
- [ ] Configure environment variables
- [ ] Build and test locally
- [ ] Deploy to your server
- [ ] Test all user accounts

---

## Database Setup

### 1. Create PostgreSQL Database
```sql
CREATE DATABASE linhs_db;
CREATE USER linhs_user WITH PASSWORD 'strong_password_here';
GRANT ALL PRIVILEGES ON DATABASE linhs_db TO linhs_user;
```

### 2. Verify Connection
```bash
psql -h localhost -U linhs_user -d linhs_db
```

---

## Local Testing

### Build the Project
```bash
cd linhs-portal
mvn clean package -DskipTests
```

### Run Development Server
```bash
# Option 1: Using Maven
mvn spring-boot:run \
  -DSPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/linhs_db \
  -DSPRING_DATASOURCE_USERNAME=linhs_user \
  -DSPRING_DATASOURCE_PASSWORD=strong_password_here

# Option 2: Using JAR
java -jar target/portal-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/linhs_db \
  --spring.datasource.username=linhs_user \
  --spring.datasource.password=strong_password_here
```

### Access the Application
- URL: http://localhost:8080
- Login with any account from FIX_SUMMARY.md
- Test each role's dashboard

---

## Docker Deployment

### Build Docker Image
```bash
docker build -t linhs-portal:v1.0.0 .
```

### Run Container
```bash
docker run -d \
  --name linhs-portal \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/linhs_db \
  -e SPRING_DATASOURCE_USERNAME=linhs_user \
  -e SPRING_DATASOURCE_PASSWORD=your_secure_password \
  linhs-portal:v1.0.0
```

### Docker Compose (Optional)
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: linhs_db
      POSTGRES_USER: linhs_user
      POSTGRES_PASSWORD: your_secure_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/linhs_db
      SPRING_DATASOURCE_USERNAME: linhs_user
      SPRING_DATASOURCE_PASSWORD: your_secure_password
    depends_on:
      - postgres

volumes:
  postgres_data:
```

Run with: `docker-compose up -d`

---

## Cloud Deployment (AWS/Azure)

### AWS RDS + EC2
1. Create RDS PostgreSQL instance
2. Security group: Allow port 5432 from EC2
3. Get RDS endpoint
4. Deploy JAR to EC2:
   ```bash
   scp target/portal-0.0.1-SNAPSHOT.jar ubuntu@your-ec2:/home/ubuntu/
   ssh ubuntu@your-ec2
   java -jar portal-0.0.1-SNAPSHOT.jar \
     --spring.datasource.url=jdbc:postgresql://your-rds-endpoint:5432/linhs_db \
     --spring.datasource.username=linhs_user \
     --spring.datasource.password=$DB_PASSWORD
   ```

### Azure App Service + Azure Database for PostgreSQL
1. Create Azure Database for PostgreSQL
2. Create App Service (Java 17)
3. Deploy with:
   ```bash
   mvn azure-webapp:deploy
   ```
4. Set App Settings:
   - SPRING_DATASOURCE_URL
   - SPRING_DATASOURCE_USERNAME
   - SPRING_DATASOURCE_PASSWORD

---

## Post-Deployment Testing

### Test Checklist
```bash
# 1. Check app is running
curl http://your-domain/

# 2. Test login endpoint
curl -X POST http://your-domain/login \
  -d "username=admin@linhs.edu.ph&password=password123"

# 3. Check logs for errors
docker logs linhs-portal  # if using Docker

# 4. Verify database connection
# Login and test document request feature
```

---

## Troubleshooting

### Issue: 500 Error on /
**Solution**: Verify index.html template exists and is accessible
```bash
ls src/main/resources/templates/index.html
```

### Issue: Login Always Fails
**Solution**: 
1. Check database has users (DataInitializer should create them)
2. Verify PostgreSQL is running and accessible
3. Check SPRING_DATASOURCE_URL, USERNAME, PASSWORD are correct

### Issue: Database Connection Failed
**Solution**:
```bash
# Test connection
psql -h <host> -U <username> -d linhs_db

# Check environment variables
echo $SPRING_DATASOURCE_URL
echo $SPRING_DATASOURCE_USERNAME
```

### Issue: Class Not Found / Compilation Errors
**Solution**: Run clean build
```bash
mvn clean package -U
```

---

## Monitoring & Maintenance

### Enable Detailed Logging
In application.properties (for debugging):
```properties
logging.level.com.linhs.portal=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Database Maintenance
```sql
-- Check table sizes
SELECT schemaname, tablename, 
  pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Backup database
pg_dump -U linhs_user linhs_db > backup_$(date +%Y%m%d).sql
```

### Regular Backups
```bash
#!/bin/bash
# backup.sh - Run daily via cron
DB_HOST="your-db-host"
DB_USER="linhs_user"
DB_NAME="linhs_db"
BACKUP_DIR="/backups"

pg_dump -h $DB_HOST -U $DB_USER $DB_NAME | gzip > \
  $BACKUP_DIR/linhs_db_$(date +%Y%m%d_%H%M%S).sql.gz

# Keep only last 30 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete
```

---

## Performance Tuning

### Database Optimization
```sql
-- Analyze query performance
EXPLAIN ANALYZE 
SELECT * FROM users WHERE email = 'admin@linhs.edu.ph';

-- Add indexes if needed
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_student_lrn ON students(lrn);
```

### Connection Pool Tuning (in application.properties)
```properties
spring.datasource.hikari.maximum-pool-size=30  # Increase for high load
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
```

---

## Support & Documentation

For issues or questions:
1. Check FIX_SUMMARY.md for all changes made
2. Review application logs for error messages
3. Verify all environment variables are set correctly
4. Test with default accounts provided
5. Check database connectivity

---

**Version**: 1.0.0  
**Last Updated**: 2026-06-05  
**Status**: Production Ready ✅
