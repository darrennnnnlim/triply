# Build stage
FROM node:22.13.0-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Serve stage
FROM nginx:1.28.0-alpine3.21-slim
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*  # Remove any default Nginx files
COPY --from=build /app/dist/triply-app/browser/* .
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
