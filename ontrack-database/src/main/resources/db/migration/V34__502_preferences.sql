-- 34. Refactoring of the preferences

DROP TABLE PREFERENCES;

CREATE TABLE PREFERENCES
(
    ACCOUNTID INTEGER NOT NULL,
    CONTENT   JSONB   NOT NULL,
    CONSTRAINT PREFERENCES_PK PRIMARY KEY (ACCOUNTID),
    CONSTRAINT PREFERENCES_FK_ACCOUNT FOREIGN KEY (ACCOUNTID) REFERENCES ACCOUNTS (ID) ON DELETE CASCADE
);