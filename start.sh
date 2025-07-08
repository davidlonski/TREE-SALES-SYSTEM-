#!/bin/bash

# Tree Sales Management System - Startup Script
# This script helps start the application with different configurations

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if port is available
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        return 1
    else
        return 0
    fi
}

print_banner() {
    echo "=========================================="
    echo "  Tree Sales Management System Web App"
    echo "=========================================="
    echo ""
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command_exists java; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_error "Java 17 or higher is required. Current version: $JAVA_VERSION"
        exit 1
    fi
    
    if ! command_exists mvn; then
        print_error "Maven is not installed. Please install Maven 3.6+"
        exit 1
    fi
    
    print_status "Prerequisites check passed ✓"
}

# Setup database
setup_database() {
    print_status "Setting up database connection..."
    
    # Check if database configuration exists
    if [ ! -f "src/main/resources/application-local.properties" ]; then
        print_warning "Creating default database configuration..."
        cat > src/main/resources/application-local.properties << EOF
# Local development database configuration
spring.datasource.url=jdbc:mariadb://localhost:3306/tree_sales_db
spring.datasource.username=treesales
spring.datasource.password=password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
EOF
        print_status "Created application-local.properties with default settings"
        print_warning "Please update database credentials in src/main/resources/application-local.properties"
    fi
}

# Build application
build_application() {
    print_status "Building application..."
    
    if ! mvn clean compile; then
        print_error "Build failed. Please check for compilation errors."
        exit 1
    fi
    
    print_status "Build completed successfully ✓"
}

# Start application
start_application() {
    local profile=${1:-local}
    local port=${2:-8080}
    
    print_status "Starting application with profile: $profile on port: $port"
    
    # Check if port is available
    if ! check_port $port; then
        print_error "Port $port is already in use. Please stop the service using that port or choose a different port."
        exit 1
    fi
    
    # Set environment variables
    export SPRING_PROFILES_ACTIVE=$profile
    export SERVER_PORT=$port
    
    # Start the application
    print_status "Starting Spring Boot application..."
    mvn spring-boot:run -Dspring-boot.run.profiles=$profile -Dserver.port=$port
}

# Docker setup
docker_setup() {
    print_status "Setting up Docker environment..."
    
    if ! command_exists docker; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command_exists docker-compose; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    print_status "Building and starting Docker containers..."
    docker-compose up --build -d
    
    print_status "Docker containers started successfully ✓"
    print_status "Application will be available at: http://localhost:8080"
    print_status "Database will be available at: localhost:3306"
}

# Show help
show_help() {
    echo "Tree Sales Management System - Startup Script"
    echo ""
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  start [profile] [port]  Start the application (default: local profile, port 8080)"
    echo "  docker                  Start using Docker Compose"
    echo "  build                   Build the application only"
    echo "  test                    Run tests"
    echo "  clean                   Clean build artifacts"
    echo "  help                    Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start                 # Start with local profile on port 8080"
    echo "  $0 start local 8081      # Start with local profile on port 8081"
    echo "  $0 docker                # Start using Docker"
    echo "  $0 build                 # Build only"
    echo ""
}

# Main script logic
main() {
    print_banner
    
    case "${1:-start}" in
        "start")
            check_prerequisites
            setup_database
            build_application
            start_application "${2:-local}" "${3:-8080}"
            ;;
        "docker")
            docker_setup
            ;;
        "build")
            check_prerequisites
            build_application
            ;;
        "test")
            check_prerequisites
            print_status "Running tests..."
            mvn test
            ;;
        "clean")
            print_status "Cleaning build artifacts..."
            mvn clean
            print_status "Clean completed ✓"
            ;;
        "help")
            show_help
            ;;
        *)
            print_error "Unknown command: $1"
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"