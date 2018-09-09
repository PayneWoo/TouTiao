######## Dockerfile来源
FROM hub.c.163.com/bingohuang/jdk8:latest

######## 编写Dockerfiler人员邮箱
MAINTAINER Payne (payne2304@163.com)

COPY target/toutiao-0.0.1-SNAPSHOT.jar toutiao.jar

ENTRYPOINT ["java","-jar","/toutiao.jar"]  
