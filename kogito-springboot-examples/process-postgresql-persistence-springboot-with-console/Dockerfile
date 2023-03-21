FROM maven:3.8.6-amazoncorretto-11

WORKDIR /app
 
COPY . /app 

RUN mvn clean package  

CMD ["mvn","clean","compile","spring-boot:run"]