# Multi-stage build for testing and production
FROM python:3.11-alpine AS builder
RUN apk add --no-cache make g++
WORKDIR /app

# Copy requirements and install dependencies
COPY requirements.txt test-requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt -r test-requirements.txt

# Copy source code and run tests
COPY . .
RUN python -m pytest test_email_server.py -v

# Production stage
FROM python:3.11-alpine AS production
RUN apk add --no-cache make g++
WORKDIR /app
ENV PORT=7004 
ENV DISABLE_PROFILER=1

# Copy requirements and install only production dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy source code
COPY . .

EXPOSE 7004
CMD ["python", "email_server.py"]