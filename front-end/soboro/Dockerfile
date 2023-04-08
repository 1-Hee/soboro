FROM node:18.13.0 AS build
RUN mkdir -p /home/soboro
WORKDIR /home/soboro
COPY . .
RUN mkdir -p build
RUN npm i --legacy-peer-deps
RUN npm run build

FROM nginx
COPY --from=build /home/soboro/build /usr/share/nginx/html
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]