FROM mongo:4.4.10

RUN apt-get update && apt-get install -y curl

COPY launch.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/launch.sh

CMD ["bash", "-c", "/usr/local/bin/launch.sh"]