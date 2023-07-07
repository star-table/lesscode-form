FROM registry.cn-shanghai.aliyuncs.com/polaris-team/openjdk:8-jdk-alpine-crypto-ttf-dejavu

ENV LANG zh_CN.UTF-8

#RUN echo -e "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main\n\
#https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/community" > /etc/apk/repositories

#RUN apk --update add curl bash ttf-dejavu && \
#      rm -rf /var/cache/apk/*

COPY form-service/target/lesscode-form-service.jar /data/app/lesscode-form/lesscode-form.jar
COPY bin /data/app/lesscode-form/bin

WORKDIR /data/app/lesscode-form/bin

RUN sed -i 's/bash/sh/g' *.sh \
    && echo -e "\ntail -f /dev/null" >> start.sh \
    && cat start.sh

EXPOSE 10667

CMD ["./start.sh"]