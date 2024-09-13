FROM bellsoft/liberica-openjdk-alpine:17
  
WORKDIR /opt/app 
  
COPY ./build/libs/*.jar .
  
CMD  java -Dspring.profiles.active=$ACTIVE_PROFILE -jar *.jar
