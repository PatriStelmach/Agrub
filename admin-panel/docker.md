## COMPOSE

    frontend:
      build:
        context: ./admin-panel
      container_name: admin-panel
      restart: unless-stopped
      ports:
        - "5173:10000"
      networks:
        - alert-network
      depends_on:
        - backend



  ## DOCKERFILE

    ARG NODE_VERSION=22.16.0-alpine
    ARG NGINX_VERSION=alpine3.22
    
    FROM node:${NODE_VERSION} as builder
    
    WORKDIR /app
    
    COPY package.json package-lock.json* ./
    
    RUN --mount=type=cache,target=/root/.npm npm ci
    
    COPY . .
    
    RUN npm run build
    
    FROM nginxinc/nginx-unprivileged:${NGINX_VERSION} AS runner
    
    COPY nginx.conf /etc/nginx/nginx.conf
    
    COPY --chown=nginx:nginx --from=builder /app/dist /usr/share/nginx/html
    
    USER nginx
    
    EXPOSE 10000
    
    ENTRYPOINT ["nginx", "-c", "/etc/nginx/nginx.conf"]
    CMD ["-g", "daemon off;"]

## NGINX.CONF

    worker_processes auto;
    pid /tmp/nginx.pid;
    
    events {
        worker_connections 2048;
    }
    
    http {
        include       /etc/nginx/mime.types;
        default_type  application/octet-stream;
        charset       utf-8;
    
        access_log    off;
        error_log     /dev/stderr warn;
    
        sendfile        on;
        tcp_nopush      on;
        tcp_nodelay     on;
        keepalive_timeout  65;
        keepalive_requests 1000;
    
        gzip on;
        gzip_comp_level 6;
        gzip_proxied any;
        gzip_min_length 256;
        gzip_vary on;
        gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript image/svg+xml;
    
        server {
            listen       10000;
            server_name  localhost;
    
            root   /usr/share/nginx/html;
            index  index.html;
    
            location / {
                try_files $uri $uri/ /index.html;
            }
    
            location /api/alerts/stream {
                proxy_pass http://backend:10000;
    
                proxy_buffering off;
                proxy_cache off;
                proxy_read_timeout 24h;
                proxy_send_timeout 24h;
    
                proxy_http_version 1.1;
                proxy_set_header Connection '';
                proxy_set_header Host $host;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Accel-Buffering no;
                proxy_set_header Cache-Control no-cache;
    
                chunked_transfer_encoding on;
                gzip off;
            }
    
            location /api/ {
                proxy_pass http://backend:10000;
                proxy_set_header Host $host;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                    }
    
            location ~* \.(?:ico|css|js|gif|jpe?g|png|woff2?|eot|ttf|svg|map)$ {
                expires 1y;
                access_log off;
                add_header Cache-Control "public, immutable";
                add_header X-Content-Type-Options nosniff;
            }
    
            location /assets/ {
                expires 1y;
                add_header Cache-Control "public, immutable";
                add_header X-Content-Type-Options nosniff;
            }
    
            error_page 404 /index.html;
        }
    }

## .DOCKERIGNORE

    # Dependency directories
    # -------------------------------
    node_modules/
    
    # -------------------------------
    # Production and build outputs
    # -------------------------------
    dist/
    out/
    build/
    public/build/
    
    # -------------------------------
    # Vite, VuePress, and cache dirs
    # -------------------------------
    .vite/
    .vitepress/
    .cache/
    .tmp/
