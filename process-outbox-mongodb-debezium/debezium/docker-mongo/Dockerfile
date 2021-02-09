FROM mongo:4.4

LABEL maintainer="Debezium Community"

COPY init-kogito.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/init-kogito.sh

COPY --chown=999:999 keyfile /usr/local/bin
RUN chmod 400 /usr/local/bin/keyfile

CMD ["mongod", "--replSet", "rs0", "--auth",  "--keyFile", "/usr/local/bin/keyfile"]
