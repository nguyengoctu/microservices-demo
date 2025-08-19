# Start from the official Golang image for building
FROM golang:1.23-alpine AS builder

WORKDIR /app

# Copy go mod and sum files
COPY go.mod go.sum ./
RUN go mod download

# Copy the source code
COPY . .

# Build the Go app
RUN CGO_ENABLED=0 GOOS=linux go build -o productcatalogservice .

# Use a minimal image for running
FROM alpine:3.18

WORKDIR /app
ENV PORT=7002

# Copy the binary from the builder
COPY --from=builder /app/productcatalogservice .
COPY products.json .

# Expose service port (change if needed)
EXPOSE 7002

ENTRYPOINT ["./productcatalogservice"]