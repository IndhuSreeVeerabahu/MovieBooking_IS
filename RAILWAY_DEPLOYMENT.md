# Railway Deployment Guide for Movie Ticket Booking System

This guide will help you deploy your Spring Boot Movie Ticket Booking System to Railway platform.

## Prerequisites

1. **Railway Account**: Sign up at [railway.app](https://railway.app)
2. **GitHub Repository**: Your code should be pushed to GitHub
3. **MySQL Database**: Railway provides MySQL addon

## Step-by-Step Deployment

### 1. Prepare Your Repository

Ensure your repository has all the deployment files:
- ✅ `railway.json` - Railway configuration
- ✅ `Dockerfile` - Container configuration
- ✅ `Procfile` - Process configuration
- ✅ `src/main/resources/application-prod.properties` - Production config
- ✅ Updated `pom.xml` - Build configuration

### 2. Deploy to Railway

#### Option A: Deploy from GitHub (Recommended)

1. **Login to Railway**: Go to [railway.app](https://railway.app) and sign in
2. **Create New Project**: Click "New Project"
3. **Deploy from GitHub**: Select "Deploy from GitHub repo"
4. **Select Repository**: Choose your `MovieBooking_IS` repository
5. **Select Branch**: Choose `master` branch
6. **Railway will automatically detect**: Java/Maven project and start building

#### Option B: Deploy with Railway CLI

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Initialize project
railway init

# Deploy
railway up
```

### 3. Configure Environment Variables

In your Railway dashboard, go to **Variables** tab and add:

#### Database Configuration
```
DATABASE_URL=mysql://username:password@host:port/database_name
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

#### JWT Configuration
```
JWT_SECRET=your-super-secret-jwt-key-at-least-32-characters-long
```

#### Email Configuration (Optional)
```
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_FROM=noreply@yourdomain.com
```

#### Cashfree Payment (Optional)
```
CASHFREE_APP_ID=your-cashfree-app-id
CASHFREE_SECRET_KEY=your-cashfree-secret-key
CASHFREE_ENVIRONMENT=SANDBOX
```

#### Application Configuration
```
BASE_URL=https://your-app-name.railway.app
CORS_ORIGINS=https://your-app-name.railway.app
SPRING_PROFILES_ACTIVE=prod
```

### 4. Add MySQL Database

1. In Railway dashboard, click **+ New**
2. Select **Database** → **MySQL**
3. Railway will automatically create and configure the database
4. Copy the connection details to your environment variables

### 5. Configure Domain (Optional)

1. Go to **Settings** → **Domains**
2. Railway provides a default domain: `your-app-name.railway.app`
3. You can add a custom domain if needed

## Environment Variables Reference

| Variable | Description | Required | Example |
|----------|-------------|----------|---------|
| `DATABASE_URL` | MySQL connection URL | Yes | `mysql://user:pass@host:port/db` |
| `DB_USERNAME` | Database username | Yes | `root` |
| `DB_PASSWORD` | Database password | Yes | `yourpassword` |
| `JWT_SECRET` | JWT signing key | Yes | `your-secret-key-32-chars` |
| `BASE_URL` | Your app URL | Yes | `https://your-app.railway.app` |
| `CORS_ORIGINS` | Allowed origins | Yes | `https://your-app.railway.app` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | Yes | `prod` |
| `MAIL_USERNAME` | Email username | No | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Email password | No | `your-app-password` |
| `CASHFREE_APP_ID` | Cashfree app ID | No | `your-app-id` |
| `CASHFREE_SECRET_KEY` | Cashfree secret | No | `your-secret-key` |

## Deployment Checklist

- [ ] Repository pushed to GitHub
- [ ] All deployment files created
- [ ] Railway project created
- [ ] MySQL database added
- [ ] Environment variables configured
- [ ] Build successful
- [ ] Application accessible via Railway URL

## Troubleshooting

### Common Issues

1. **Build Fails**
   - Check Java version (should be 17)
   - Verify Maven dependencies
   - Check for compilation errors

2. **Database Connection Issues**
   - Verify `DATABASE_URL` format
   - Check database credentials
   - Ensure database is running

3. **Application Won't Start**
   - Check environment variables
   - Verify port configuration (Railway uses `PORT` env var)
   - Check application logs in Railway dashboard

4. **CORS Issues**
   - Update `CORS_ORIGINS` with your Railway domain
   - Check `BASE_URL` configuration

### Logs and Debugging

1. **View Logs**: Go to Railway dashboard → Your project → **Deployments** → Click on deployment → **View Logs**
2. **Debug Mode**: Add `logging.level.com.example.MovieTicketBooking=DEBUG` to environment variables

## Post-Deployment

1. **Test Application**: Visit your Railway URL
2. **Create Admin User**: Register and update role to ADMIN in database
3. **Configure Email**: Set up email service for notifications
4. **Setup Payment**: Configure Cashfree for payment processing

## Default Admin Account

After deployment, you can create an admin user by:
1. Registering a new user through the web interface
2. Manually updating the user role in the database:
   ```sql
   UPDATE user SET role = 'ADMIN' WHERE email = 'your-email@example.com';
   ```

## Monitoring

Railway provides:
- **Metrics**: CPU, Memory, Network usage
- **Logs**: Real-time application logs
- **Deployments**: Deployment history and status

## Cost Optimization

- Railway offers free tier with limited resources
- Monitor usage in dashboard
- Upgrade plan if needed for production use

## Security Notes

- Never commit sensitive data to repository
- Use environment variables for all secrets
- Regularly rotate JWT secrets
- Keep dependencies updated

## Support

- Railway Documentation: [docs.railway.app](https://docs.railway.app)
- Railway Discord: [discord.gg/railway](https://discord.gg/railway)
- GitHub Issues: Create issues in your repository

---

**Your app will be available at**: `https://your-app-name.railway.app`

Happy deploying! 🚀
