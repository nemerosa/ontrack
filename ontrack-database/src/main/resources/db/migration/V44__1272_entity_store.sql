-- 44. Simple entity store

CREATE TABLE ENTITY_STORE
(
    ID               SERIAL PRIMARY KEY NOT NULL,
    PROJECT          INTEGER,
    BRANCH           INTEGER,
    PROMOTION_LEVEL  INTEGER,
    VALIDATION_STAMP INTEGER,
    BUILD            INTEGER,
    PROMOTION_RUN    INTEGER,
    VALIDATION_RUN   INTEGER,
    STORE            VARCHAR(200)       NOT NULL,
    NAME             VARCHAR(150)       NOT NULL,
    DATA             JSONB              NOT NULL,
    CONSTRAINT ENTITY_STORE_FK_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCHES (ID) ON DELETE CASCADE,
    CONSTRAINT ENTITY_STORE_FK_BUILD FOREIGN KEY (BUILD) REFERENCES BUILDS (ID) ON DELETE CASCADE,
    CONSTRAINT ENTITY_STORE_FK_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECTS (ID) ON DELETE CASCADE,
    CONSTRAINT ENTITY_STORE_FK_PROMOTION_LEVEL FOREIGN KEY (PROMOTION_LEVEL) REFERENCES PROMOTION_LEVELS (ID) ON DELETE CASCADE,
    CONSTRAINT ENTITY_STORE_FK_PROMOTION_RUN FOREIGN KEY (PROMOTION_RUN) REFERENCES PROMOTION_RUNS (ID) ON DELETE CASCADE,
    CONSTRAINT ENTITY_STORE_FK_VALIDATION_RUN FOREIGN KEY (VALIDATION_RUN) REFERENCES VALIDATION_RUNS (ID) ON DELETE CASCADE,
    CONSTRAINT ENTITY_STORE_FK_VALIDATION_STAMP FOREIGN KEY (VALIDATION_STAMP) REFERENCES VALIDATION_STAMPS (ID) ON DELETE CASCADE
);

CREATE INDEX ENTITY_STORE_IX_BRANCH ON ENTITY_STORE (BRANCH);
CREATE INDEX ENTITY_STORE_IX_BUILD ON ENTITY_STORE (BUILD);
CREATE INDEX ENTITY_STORE_IX_PROJECT ON ENTITY_STORE (PROJECT);
CREATE INDEX ENTITY_STORE_IX_PROMOTION_LEVEL ON ENTITY_STORE (PROMOTION_LEVEL);
CREATE INDEX ENTITY_STORE_IX_PROMOTION_RUN ON ENTITY_STORE (PROMOTION_RUN);
CREATE INDEX ENTITY_STORE_IX_VALIDATION_RUN ON ENTITY_STORE (VALIDATION_RUN);
CREATE INDEX ENTITY_STORE_IX_VALIDATION_STAMP ON ENTITY_STORE (VALIDATION_STAMP);

CREATE INDEX ENTITY_STORE_IX_STORE_BRANCH ON ENTITY_STORE (BRANCH, STORE);
CREATE INDEX ENTITY_STORE_IX_STORE_BUILD ON ENTITY_STORE (BUILD, STORE);
CREATE INDEX ENTITY_STORE_IX_STORE_PROJECT ON ENTITY_STORE (PROJECT, STORE);
CREATE INDEX ENTITY_STORE_IX_STORE_PROMOTION_LEVEL ON ENTITY_STORE (PROMOTION_LEVEL, STORE);
CREATE INDEX ENTITY_STORE_IX_STORE_PROMOTION_RUN ON ENTITY_STORE (PROMOTION_RUN, STORE);
CREATE INDEX ENTITY_STORE_IX_STORE_VALIDATION_RUN ON ENTITY_STORE (VALIDATION_RUN, STORE);
CREATE INDEX ENTITY_STORE_IX_STORE_VALIDATION_STAMP ON ENTITY_STORE (VALIDATION_STAMP, STORE);

CREATE UNIQUE INDEX ENTITY_STORE_NAME_UQ_STORE_BRANCH ON ENTITY_STORE (BRANCH, STORE, NAME);
CREATE UNIQUE INDEX ENTITY_STORE_NAME_UQ_STORE_BUILD ON ENTITY_STORE (BUILD, STORE, NAME);
CREATE UNIQUE INDEX ENTITY_STORE_NAME_UQ_STORE_PROJECT ON ENTITY_STORE (PROJECT, STORE, NAME);
CREATE UNIQUE INDEX ENTITY_STORE_NAME_UQ_STORE_PROMOTION_LEVEL ON ENTITY_STORE (PROMOTION_LEVEL, STORE, NAME);
CREATE UNIQUE INDEX ENTITY_STORE_NAME_UQ_STORE_PROMOTION_RUN ON ENTITY_STORE (PROMOTION_RUN, STORE, NAME);
CREATE UNIQUE INDEX ENTITY_STORE_NAME_UQ_STORE_VALIDATION_RUN ON ENTITY_STORE (VALIDATION_RUN, STORE, NAME);
CREATE UNIQUE INDEX ENTITY_STORE_NAME_UQ_STORE_VALIDATION_STAMP ON ENTITY_STORE (VALIDATION_STAMP, STORE, NAME);
