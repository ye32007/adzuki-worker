FROM java:8-alpine
RUN apk update && apk add tzdata
RUN cp "/usr/share/zoneinfo/Asia/Shanghai" /etc/localtime && echo "Asia/Shanghai" > /etc/timezone
VOLUME /tmp
WORKDIR /usr/app
ADD ./target/adzuki-worker.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]