# Deployment Guide

## Local Development Setup

### 1. Install Prerequisites
```bash
# Install Java 17
sudo apt install openjdk-17-jdk  # Ubuntu/Debian
brew install openjdk@17          # macOS

# Install Maven
sudo apt install maven           # Ubuntu/Debian
brew install maven               # macOS

# Install MongoDB
# Ubuntu/Debian
sudo apt install mongodb

# macOS
brew tap mongodb/brew
brew install mongodb-community

# Install Redis
sudo apt install redis-server    # Ubuntu/Debian
brew install redis               # macOS
```

### 2. Start Services
```bash
# Start MongoDB
sudo systemctl start mongodb     # Linux
brew services start mongodb-community  # macOS

# Start Redis
sudo systemctl start redis       # Linux
brew services start redis        # macOS
```

### 3. Configure Application
```bash
# Copy environment template
cp .env.example .env

# Edit .env with your configuration
nano .env
```

### 4. Build and Run
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Docker Deployment

### Using Docker Compose (Recommended)

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Manual Docker Build

```bash
# Build application
mvn clean package -DskipTests

# Build Docker image
docker build -t expense-tracker:latest .

# Run container
docker run -p 8080:8080 \
  --name expense-tracker \
  --env-file .env \
  expense-tracker:latest
```

---

## Cloud Deployment

### AWS Elastic Beanstalk

1. **Install EB CLI**
```bash
pip install awsebcli
```

2. **Initialize EB**
```bash
eb init -p docker expense-tracker
```

3. **Create environment**
```bash
eb create expense-tracker-env
```

4. **Deploy**
```bash
eb deploy
```

5. **Set environment variables**
```bash
eb setenv \
  MONGODB_URI=mongodb://... \
  REDIS_HOST=... \
  JWT_SECRET=... \
  AWS_ACCESS_KEY=... \
  AWS_SECRET_KEY=...
```

---

### AWS EC2

1. **Launch EC2 instance** (Amazon Linux 2)

2. **Connect to instance**
```bash
ssh -i your-key.pem ec2-user@your-instance-ip
```

3. **Install Docker**
```bash
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user
```

4. **Clone and deploy**
```bash
git clone <your-repo>
cd SpringbootFinalProject
docker-compose up -d
```

---

### Heroku

1. **Install Heroku CLI**
```bash
curl https://cli-assets.heroku.com/install.sh | sh
```

2. **Login**
```bash
heroku login
```

3. **Create app**
```bash
heroku create expense-tracker-app
```

4. **Add MongoDB and Redis**
```bash
heroku addons:create mongolab
heroku addons:create heroku-redis
```

5. **Set environment variables**
```bash
heroku config:set JWT_SECRET=your_secret
heroku config:set AWS_ACCESS_KEY=your_key
# ... other variables
```

6. **Deploy**
```bash
git push heroku main
```

---

### Google Cloud Platform (Cloud Run)

1. **Build container**
```bash
gcloud builds submit --tag gcr.io/PROJECT_ID/expense-tracker
```

2. **Deploy to Cloud Run**
```bash
gcloud run deploy expense-tracker \
  --image gcr.io/PROJECT_ID/expense-tracker \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

3. **Set environment variables**
```bash
gcloud run services update expense-tracker \
  --set-env-vars="MONGODB_URI=...,REDIS_HOST=..."
```

---

### Azure App Service

1. **Create resource group**
```bash
az group create --name ExpenseTrackerRG --location eastus
```

2. **Create app service plan**
```bash
az appservice plan create \
  --name ExpenseTrackerPlan \
  --resource-group ExpenseTrackerRG \
  --sku B1 \
  --is-linux
```

3. **Create web app**
```bash
az webapp create \
  --resource-group ExpenseTrackerRG \
  --plan ExpenseTrackerPlan \
  --name expense-tracker-app \
  --deployment-container-image-name expense-tracker:latest
```

---

## Production Checklist

### Security
- [ ] Change JWT_SECRET to strong random value
- [ ] Use strong database passwords
- [ ] Enable HTTPS/SSL
- [ ] Configure CORS properly
- [ ] Enable rate limiting
- [ ] Set up firewall rules
- [ ] Use environment variables for secrets

### Database
- [ ] Set up MongoDB Atlas (managed)
- [ ] Configure database backups
- [ ] Enable authentication
- [ ] Create indexes
- [ ] Set up replication

### Caching
- [ ] Use Redis Cloud or AWS ElastiCache
- [ ] Configure Redis password
- [ ] Set up Redis persistence

### Monitoring
- [ ] Set up application logs
- [ ] Configure error tracking (Sentry)
- [ ] Monitor API performance
- [ ] Set up health checks
- [ ] Configure alerts

### Storage
- [ ] Configure AWS S3 bucket policies
- [ ] Enable S3 versioning
- [ ] Set up CDN (CloudFront)
- [ ] Configure CORS for S3

### Email
- [ ] Use SendGrid or AWS SES
- [ ] Configure SPF/DKIM
- [ ] Set up email templates

---

## Environment-specific Configurations

### Development
```yaml
spring:
  profiles: dev
logging:
  level:
    root: DEBUG
```

### Production
```yaml
spring:
  profiles: prod
logging:
  level:
    root: WARN
    com.expensetracker: INFO
```

---

## Scaling Considerations

### Horizontal Scaling
- Use load balancer (AWS ALB, Nginx)
- Deploy multiple application instances
- Use session-less architecture (JWT)
- Shared Redis for caching

### Vertical Scaling
- Increase instance size
- Optimize JVM settings
```bash
java -Xms512m -Xmx2048m -jar app.jar
```

### Database Scaling
- MongoDB sharding
- Read replicas
- Connection pooling

---

## Backup Strategy

### Database Backup
```bash
# MongoDB backup
mongodump --uri="mongodb://..." --out=/backup/$(date +%Y%m%d)

# Automated backup script
0 2 * * * /scripts/backup-mongodb.sh
```

### Application Backup
- Use git tags for releases
- Store configuration separately
- Back up S3 bucket
- Document deployment process

---

## Troubleshooting

### Application won't start
- Check logs: `docker-compose logs app`
- Verify MongoDB connection
- Check Redis connection
- Validate environment variables

### Database connection issues
- Test connectivity: `mongo mongodb://...`
- Check firewall rules
- Verify credentials
- Check network settings

### High memory usage
- Monitor with: `docker stats`
- Adjust JVM heap size
- Check for memory leaks
- Review caching strategy

---

## Maintenance

### Regular Updates
```bash
# Update dependencies
mvn versions:display-dependency-updates

# Security updates
mvn versions:display-plugin-updates

# Rebuild and redeploy
mvn clean package
docker-compose up -d --build
```

### Database Maintenance
```bash
# MongoDB
db.runCommand({ compact: 'expenses' })

# Redis
redis-cli FLUSHDB  # Clear cache if needed
```

---

## Support

For deployment issues:
- Check application logs
- Review Docker logs
- Consult cloud provider documentation
- Create issue in repository
