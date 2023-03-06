FROM library/mongo:4.4.14

RUN apt-get update && apt-get install -y curl

COPY launch.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/launch.sh

CMD ["bash", "-c", "/usr/local/bin/launch.sh"]