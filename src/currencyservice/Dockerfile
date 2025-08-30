# Multi-stage build for testing and production
FROM node:18-alpine AS builder
RUN apk add --no-cache python3 make g++
WORKDIR /usr/src/app 

# Copy package files and install all dependencies (including dev)
COPY package*.json ./
RUN npm install

# Copy source code and run tests
COPY . .
RUN npm test

# Production stage
FROM node:18-alpine AS production
RUN apk add --no-cache python3 make g++
WORKDIR /usr/src/app 
ENV DISABLE_PROFILER=1
ENV PORT=7000

# Copy package files and install only production dependencies
COPY package*.json ./
RUN npm install --omit=dev

# Copy source code
COPY . .
EXPOSE 7000
CMD ["/bin/sh", "-c", "node server.js"]