-- 41. Increase property search key max length (#639)

ALTER TABLE PROPERTIES ALTER COLUMN SEARCHKEY VARCHAR(600) NULL;
