FROM python:3.11-slim

WORKDIR /loadgen

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY locustfile.py .

EXPOSE 8089

ENTRYPOINT ["locust"]
