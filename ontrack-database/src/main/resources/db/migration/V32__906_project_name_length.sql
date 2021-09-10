-- 32. Increasing the max length for a project name

ALTER TABLE PROJECTS ALTER COLUMN NAME TYPE VARCHAR(80);
