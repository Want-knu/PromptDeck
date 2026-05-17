# ── Build stage ─────────────────────────────────────────────────
FROM node:20-alpine AS build
WORKDIR /app

# 의존성 레이어 캐시
COPY package.json ./
RUN npm install

COPY . .
RUN npm run build

# ── Runtime stage (Nginx) ────────────────────────────────────────
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]
