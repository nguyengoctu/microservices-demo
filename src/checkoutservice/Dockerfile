# Start from the official Golang image for building
FROM golang:1.23-alpine AS builder

WORKDIR /app

# Copy go mod and sum files
COPY go.mod go.sum ./
RUN go mod download

# Copy the source code
COPY . .

# Run tests
RUN go test -v ./...

# Build the Go app
RUN CGO_ENABLED=0 GOOS=linux go build -o checkout-service .

# Use a minimal image for running
FROM alpine:3.18

WORKDIR /app
ENV PORT=7008
ENV DISABLE_PROFILER=1

ENV CURRENCY_SERVICE_ADDR=currency-service:7000
ENV PAYMENT_SERVICE_ADDR=payment-service:7001
ENV PRODUCT_CATALOG_SERVICE_ADDR=product-catalog-service:7002
ENV SHIPPING_SERVICE_ADDR=shipping-service:7003
ENV EMAIL_SERVICE_ADDR=email-service:7004
ENV CART_SERVICE_ADDR=cart-service:7006

# Copy the binary from the builder
COPY --from=builder /app/checkout-service .

# Expose service port (change if needed)
EXPOSE 7008

ENTRYPOINT ["./checkout-service"]