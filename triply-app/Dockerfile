# Serve stage
FROM nginx:alpine
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*  # Ensure no default Nginx files
COPY ./dist/triply-app .  # Copy artifacts directly to Nginx html directory
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
