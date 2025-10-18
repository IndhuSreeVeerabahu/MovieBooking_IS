#!/bin/bash

echo "========================================"
echo "Railway Deployment Script"
echo "========================================"

echo ""
echo "1. Building the application..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed! Please check the errors above."
    exit 1
fi

echo ""
echo "2. Build successful!"
echo ""
echo "3. Next steps:"
echo "   - Push your code to GitHub"
echo "   - Go to railway.app"
echo "   - Create new project from GitHub"
echo "   - Select your repository"
echo "   - Configure environment variables"
echo "   - Add MySQL database"
echo ""
echo "4. Required Environment Variables:"
echo "   - DATABASE_URL"
echo "   - DB_USERNAME"
echo "   - DB_PASSWORD"
echo "   - JWT_SECRET"
echo "   - BASE_URL"
echo "   - CORS_ORIGINS"
echo "   - SPRING_PROFILES_ACTIVE=prod"
echo ""
echo "5. Optional Environment Variables:"
echo "   - MAIL_USERNAME"
echo "   - MAIL_PASSWORD"
echo "   - CASHFREE_APP_ID"
echo "   - CASHFREE_SECRET_KEY"
echo ""
echo "Check RAILWAY_DEPLOYMENT.md for detailed instructions!"
echo ""
