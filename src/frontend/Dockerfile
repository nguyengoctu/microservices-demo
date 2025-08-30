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
RUN CGO_ENABLED=0 GOOS=linux go build -o frontend .

# Use a minimal image for running
FROM alpine:3.18

WORKDIR /app
ENV PORT=7009
ENV LISTEN_ADDR=0.0.0.0
ENV DISABLE_PROFILER=1

ENV CURRENCY_SERVICE_ADDR=currency-service:7000
ENV PRODUCT_CATALOG_SERVICE_ADDR=product-catalog-service:7002
ENV SHIPPING_SERVICE_ADDR=shipping-service:7003
ENV RECOMMENDATION_SERVICE_ADDR=recommendation-service:7005
ENV CART_SERVICE_ADDR=cart-service:7006
ENV AD_SERVICE_ADDR=ad-service:7007
ENV CHECKOUT_SERVICE_ADDR=checkout-service:7008
ENV SHOPPING_ASSISTANT_SERVICE_ADDR=localhost:8080

# Copy the binary from the builder
COPY --from=builder /app/frontend .
COPY templates /app/templates
COPY static /app/static

# Expose service port (change if needed)
EXPOSE 7009

CMD ["./frontend"]