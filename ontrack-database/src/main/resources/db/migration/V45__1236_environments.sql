-- 445. Environments

CREATE TABLE ENVIRONMENTS
(
    ID          VARCHAR(36)  NOT NULL PRIMARY KEY,
    NAME        VARCHAR(128) NOT NULL,
    "ORDER"     INTEGER      NOT NULL,
    DESCRIPTION VARCHAR(500) NULL
);

CREATE UNIQUE INDEX ENVIRONMENTS_UQ_NAME ON ENVIRONMENTS (NAME);

CREATE TABLE ENV_SLOTS
(
    ID             VARCHAR(36)  NOT NULL PRIMARY KEY,
    ENVIRONMENT_ID VARCHAR(36)  NOT NULL,
    PROJECT_ID     INTEGER      NOT NULL,
    QUALIFIER      VARCHAR(128) NOT NULL,
    DESCRIPTION    VARCHAR(500) NULL,
    CONSTRAINT ENV_SLOTS_FK_ENVIRONMENT FOREIGN KEY (ENVIRONMENT_ID) REFERENCES ENVIRONMENTS (ID) ON DELETE CASCADE,
    CONSTRAINT ENV_SLOTS_FK_PROJECT FOREIGN KEY (PROJECT_ID) REFERENCES PROJECTS (ID) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ENV_SLOTS_UQ_ENV_PROJECT_QUALIFIER ON ENV_SLOTS (ENVIRONMENT_ID, PROJECT_ID, QUALIFIER);

CREATE TABLE ENV_SLOT_ADMISSION_RULE_CONFIGS
(
    ID          VARCHAR(36)  NOT NULL PRIMARY KEY,
    SLOT_ID     VARCHAR(36)  NOT NULL,
    NAME        VARCHAR(256) NOT NULL,
    DESCRIPTION VARCHAR(500) NULL,
    RULE_ID     VARCHAR(128) NOT NULL,
    RULE_CONFIG JSONB        NOT NULL,
    CONSTRAINT ENV_SLOT_ADMISSION_RULE_CONFIGS_FK_ENV_SLOT FOREIGN KEY (SLOT_ID) REFERENCES ENV_SLOTS (ID) ON DELETE CASCADE
);

CREATE TABLE ENV_SLOT_PIPELINE
(
    ID       VARCHAR(36) NOT NULL PRIMARY KEY,
    SLOT_ID  VARCHAR(36) NOT NULL,
    BUILD_ID INTEGER     NOT NULL,
    START    VARCHAR(24) NOT NULL,
    "END"    VARCHAR(24) NULL,
    STATUS   VARCHAR(24) NOT NULL,
    CONSTRAINT ENV_SLOT_PIPELINE_FK_ENV_SLOT FOREIGN KEY (SLOT_ID) REFERENCES ENV_SLOTS (ID) ON DELETE CASCADE,
    CONSTRAINT ENV_SLOT_PIPELINE_FK_BUILD FOREIGN KEY (BUILD_ID) REFERENCES BUILDS (ID) ON DELETE CASCADE
);

CREATE TABLE ENV_SLOT_PIPELINE_CHANGE
(
    ID               VARCHAR(36)  NOT NULL PRIMARY KEY,
    PIPELINE_ID      VARCHAR(36)  NOT NULL,
    "USER"           VARCHAR(40)  NOT NULL,
    TIMESTAMP        VARCHAR(24)  NOT NULL,
    STATUS           VARCHAR(24)  NULL,
    MESSAGE          VARCHAR(500) NULL,
    OVERRIDE         BOOLEAN      NOT NULL,
    OVERRIDE_MESSAGE VARCHAR(500) NULL,
    CONSTRAINT ENV_SLOT_PIPELINE_CHANGE_FK_ENV_SLOT_PIPELINE FOREIGN KEY (PIPELINE_ID) REFERENCES ENV_SLOT_PIPELINE (ID) ON DELETE CASCADE
);
