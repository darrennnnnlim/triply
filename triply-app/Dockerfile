# Build stage
FROM node:22.13.0-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Serve stage
FROM nginx:alpine
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*  # Ensure no default Nginx files
COPY --from=build /app/dist/triply-app .  # Update to match the Angular output path
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
