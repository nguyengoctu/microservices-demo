FROM python:3.11-alpine
RUN apk add --no-cache make g++
WORKDIR /app
ENV PORT=7004 
ENV DISABLE_PROFILER=1

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .

EXPOSE 7004
CMD ["python", "email_server.py"]