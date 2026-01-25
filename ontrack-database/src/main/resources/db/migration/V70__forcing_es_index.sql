-- 70. Forcing the indexes to be created anew

DELETE FROM STORAGE
WHERE STORE = 'net.nemerosa.ontrack.service.elasticsearch.ElasticSearchV5Migration'
AND NAME = 'migration';
