FROM library/mongo:4.4.14

COPY init.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/init.sh

COPY launch.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/launch.sh

RUN openssl rand -base64 756 > /usr/local/bin/keyfile
RUN chown 999:999 /usr/local/bin/keyfile
RUN chmod 400 /usr/local/bin/keyfile

ENTRYPOINT ["/usr/local/bin/launch.sh"]
CMD []