FROM gradle:jdk19-focal AS build
WORKDIR /app
# Copy Gradle files
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/

# Download dependencies for caching
RUN gradle dependencies --no-daemon

# Copy source code
COPY . .

# Run tests first
RUN gradle test --no-daemon -x verifyGoogleJavaFormat

# Build distribution (includes all deps properly)
RUN gradle installDist --no-daemon -x test -x check -x verifyGoogleJavaFormat

# Runtime stage - Java 19 runtime
FROM openjdk:19-jdk-slim
WORKDIR /app

# Copy entire distribution
COPY --from=build /app/build/install/hipstershop/ /app/

# Set environment
ENV PORT=7007

EXPOSE 7007

# Use generated startup script
CMD ["./bin/AdService"]