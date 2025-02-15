-- 54. Persisting the workflow status

-- /!\ The status is set to blank by default and is then migrated by WorkflowInstanceRepositoryStatusMigration

ALTER TABLE WKF_INSTANCES
    ADD COLUMN STATUS VARCHAR(16) NOT NULL DEFAULT '';
