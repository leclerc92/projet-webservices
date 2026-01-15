# Dockerfile pour GraphQLClaimTrackingService
FROM node:18-alpine

WORKDIR /app

# Copier les fichiers de dépendances
COPY package*.json ./

# Installer les dépendances
RUN npm install --production

# Copier le code source
COPY . .

EXPOSE 4000

CMD ["npm", "start"]
