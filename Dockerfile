FROM mcr.microsoft.com/playwright/java:v1.47.0-jammy

# Set working directory inside container
WORKDIR /app

# Copy Maven descriptor and resolve dependencies first (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy project source and configuration
COPY src ./src
COPY testng.xml .

# Default execution runs the master testng suite in headless mode
ENTRYPOINT ["mvn", "test"]
CMD ["-Dheadless=true"]