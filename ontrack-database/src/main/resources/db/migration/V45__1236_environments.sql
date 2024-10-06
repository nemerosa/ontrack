-- 445. Environments

CREATE TABLE ENVIRONMENTS
(
    ID          VARCHAR(36)  NOT NULL PRIMARY KEY,
    NAME        VARCHAR(128) NOT NULL,
    "ORDER"     INTEGER      NOT NULL,
    DESCRIPTION VARCHAR(500) NULL
);

CREATE UNIQUE INDEX ENVIRONMENTS_UQ_NAME ON ENVIRONMENTS (NAME);