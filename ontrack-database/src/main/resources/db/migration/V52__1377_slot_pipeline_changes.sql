-- 52. Types of changes, unique pipeline rule statuses

DELETE
FROM ENV_SLOT_PIPELINE;

ALTER TABLE ENV_SLOT_PIPELINE_CHANGE
    DROP COLUMN DATA_CHANGED,
    DROP COLUMN OVERRIDDEN,
    ADD COLUMN TYPE VARCHAR(64);

DROP TABLE ENV_SLOT_PIPELINE_ADMISSION_RULE_STATUS;

CREATE TABLE ENV_SLOT_PIPELINE_ADMISSION_RULE_STATUS
(
    PIPELINE_ID              VARCHAR(36)  NOT NULL,
    ADMISSION_RULE_CONFIG_ID VARCHAR(36)  NOT NULL,
    DATA                     JSONB        NULL,
    DATA_USER                VARCHAR(40)  NULL,
    DATA_TIMESTAMP           VARCHAR(24)  NULL,
    OVERRIDE_USER            VARCHAR(40)  NULL,
    OVERRIDE_TIMESTAMP       VARCHAR(24)  NULL,
    OVERRIDE_MESSAGE         VARCHAR(500) NULL,
    CONSTRAINT ENV_SLOT_PIPELINE_ADMISSION_RULE_STATUS_PK PRIMARY KEY (PIPELINE_ID, ADMISSION_RULE_CONFIG_ID),
    CONSTRAINT ENV_SLOT_PIPELINE_ADMISSION_RULE_STATUS_FK_ENV_SLOT_PIPELINE FOREIGN KEY (PIPELINE_ID) REFERENCES ENV_SLOT_PIPELINE (ID) ON DELETE CASCADE,
    CONSTRAINT ENV_SLOT_PIPELINE_RULE_STATUS_FK_ENV_SLOT_ADMISSION_RULE_CONFIG FOREIGN KEY (ADMISSION_RULE_CONFIG_ID) REFERENCES ENV_SLOT_ADMISSION_RULE_CONFIGS (ID) ON DELETE CASCADE
);
