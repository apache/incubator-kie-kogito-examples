CREATE ROLE "kogito-user" WITH
    LOGIN
    SUPERUSER
    INHERIT
    CREATEDB
    CREATEROLE
    NOREPLICATION
    PASSWORD 'kogito-pass';

CREATE DATABASE kogito
    WITH
    OWNER = "kogito-user"
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

GRANT ALL PRIVILEGES ON DATABASE kogito TO "kogito-user";
GRANT ALL PRIVILEGES ON DATABASE kogito TO postgres;
