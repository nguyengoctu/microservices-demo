FROM node:18-alpine
RUN apk add --no-cache python3 make g++
WORKDIR /usr/src/app 
ENV DISABLE_PROFILER=1
ENV PORT=7001
COPY package*.json ./
RUN npm install --omit=dev
COPY . .
EXPOSE 7001
CMD ["/bin/sh", "-c", "node index.js"]