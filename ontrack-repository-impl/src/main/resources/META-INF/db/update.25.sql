-- 25 . Allow build names to have a long name, up to 150 characters (#392)

ALTER TABLE BUILDS ALTER COLUMN NAME VARCHAR(150) NOT NULL;
