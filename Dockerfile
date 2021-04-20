FROM registry.access.redhat.com/ubi8/ubi-minimal
WORKDIR /work/
COPY target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8090
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
