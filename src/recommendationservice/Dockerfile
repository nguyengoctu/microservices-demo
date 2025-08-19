FROM python:3.11-alpine
RUN apk add --no-cache make g++
WORKDIR /app
ENV PORT=7005
ENV DISABLE_PROFILER=1
ENV PRODUCT_CATALOG_SERVICE_ADDR=product-catalog-service:7002

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .

EXPOSE 7005
CMD ["python", "recommendation_server.py"]