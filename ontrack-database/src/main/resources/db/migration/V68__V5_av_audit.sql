-- 68. Migration of the audit records into a table.

CREATE TABLE AV_AUDIT
(
    UUID                    VARCHAR(36) PRIMARY KEY,
    BRANCH_ID               INTEGER      NOT NULL,
    TIMESTAMP               VARCHAR(24)  NOT NULL,

    -- Source configuration
    SOURCE_PROJECT          VARCHAR(255) NOT NULL,
    SOURCE_BUILD_ID         INTEGER,
    SOURCE_PROMOTION_RUN_ID INTEGER,
    SOURCE_PROMOTION        VARCHAR(255),
    SOURCE_BACK_VALIDATION  VARCHAR(255),
    QUALIFIER               VARCHAR(255),

    -- Target configuration
    TARGET_PATHS            TEXT         NOT NULL, -- JSON array of strings
    TARGET_REGEX            VARCHAR(255),
    TARGET_PROPERTY         VARCHAR(255),
    TARGET_PROPERTY_REGEX   VARCHAR(255),
    TARGET_PROPERTY_TYPE    VARCHAR(255),
    TARGET_VERSION          VARCHAR(255) NOT NULL,

    -- Approval and upgrade configuration
    AUTO_APPROVAL           BOOLEAN      NOT NULL,
    UPGRADE_BRANCH_PATTERN  VARCHAR(255) NOT NULL,
    UPGRADE_BRANCH          VARCHAR(255),
    AUTO_APPROVAL_MODE      VARCHAR(50)  NOT NULL,

    -- Post-processing
    POST_PROCESSING         VARCHAR(255),
    POST_PROCESSING_CONFIG  JSONB,

    -- Validation
    VALIDATION_STAMP        VARCHAR(255),

    -- States
    MOST_RECENT_STATE       VARCHAR(50)  NOT NULL,
    RUNNING                 BOOLEAN      NOT NULL,
    STATES                  JSONB        NOT NULL, -- JSON array of AutoVersioningAuditEntryState objects

    -- Routing and queue
    ROUTING                 VARCHAR(255) NULL,
    QUEUE                   VARCHAR(255) NULL,

    -- Pull request configuration
    REVIEWERS               TEXT,                  -- JSON array of strings
    PR_TITLE_TEMPLATE       TEXT,
    PR_BODY_TEMPLATE        TEXT,
    PR_BODY_TEMPLATE_FORMAT VARCHAR(50),

    -- Additional paths
    ADDITIONAL_PATHS        JSONB,                 -- JSON array of AutoVersioningSourceConfigPath objects

    -- Scheduling
    SCHEDULE                VARCHAR(24),

    CONSTRAINT AV_AUDIT_FK_BRANCH FOREIGN KEY (BRANCH_ID) REFERENCES BRANCHES (ID)
);

-- Indexes for common queries
CREATE INDEX AV_AUDIT_IDX_SOURCE_PROJECT ON AV_AUDIT (SOURCE_PROJECT);
CREATE INDEX AV_AUDIT_IDX_ROUTING ON AV_AUDIT (ROUTING);
CREATE INDEX AV_AUDIT_IDX_AUTO_APPROVAL ON AV_AUDIT (AUTO_APPROVAL);
CREATE INDEX AV_AUDIT_IDX_SCHEDULE ON AV_AUDIT (SCHEDULE);

-- JSONB indexes for state queries
CREATE INDEX AV_AUDIT_IDX_STATES ON AV_AUDIT USING GIN (STATES);
CREATE INDEX AV_AUDIT_IDX_ADDITIONAL_PATHS ON AV_AUDIT USING GIN (ADDITIONAL_PATHS);

-- Index for filtering by BRANCH_ID (commonly used in queries to isolate data by project/branch)
CREATE INDEX AV_AUDIT_IDX_BRANCH_ID ON AV_AUDIT (BRANCH_ID);

-- Index for filtering by MOST_RECENT_STATE (likely used to find records in specific states)
CREATE INDEX AV_AUDIT_IDX_MOST_RECENT_STATE ON AV_AUDIT (MOST_RECENT_STATE);

-- Combined index for BRANCH_ID and MOST_RECENT_STATE (common compound filter)
CREATE INDEX AV_AUDIT_IDX_BRANCH_STATE ON AV_AUDIT (BRANCH_ID, MOST_RECENT_STATE);

-- Index for RUNNING status queries (filtering for active/in-progress operations)
CREATE INDEX AV_AUDIT_IDX_RUNNING ON AV_AUDIT (RUNNING);

-- Combined index for BRANCH_ID and RUNNING (find active operations in a branch)
CREATE INDEX AV_AUDIT_IDX_BRANCH_RUNNING ON AV_AUDIT (BRANCH_ID, RUNNING);

-- Index for QUEUE filtering (used by queue processing system)
CREATE INDEX AV_AUDIT_IDX_QUEUE ON AV_AUDIT (QUEUE);

-- Composite index for common query patterns: finding records by branch, state, and running status
CREATE INDEX AV_AUDIT_IDX_BRANCH_STATE_RUNNING ON AV_AUDIT (BRANCH_ID, MOST_RECENT_STATE, RUNNING);

-- Index for TIMESTAMP ordering (for pagination and time-based queries)
CREATE INDEX AV_AUDIT_IDX_TIMESTAMP ON AV_AUDIT (TIMESTAMP DESC);
