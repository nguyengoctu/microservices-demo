FROM node:18-alpine
RUN apk add --no-cache python3 make g++
WORKDIR /usr/src/app 
ENV DISABLE_PROFILER=1
ENV PORT=7000
COPY package*.json ./
RUN npm install --omit=dev
COPY . .
EXPOSE 7000
CMD ["/bin/sh", "-c", "node server.js"]