# Dockerfile pour le frontend
FROM nginx:alpine

# Copier les fichiers dans le dossier nginx
COPY index.html /usr/share/nginx/html/index.html
COPY getWorkflow.png /usr/share/nginx/html/getWorkflow.png
COPY postWorkflow.png /usr/share/nginx/html/postWorkflow.png

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
