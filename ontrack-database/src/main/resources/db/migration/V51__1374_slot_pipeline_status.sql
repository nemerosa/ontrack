-- 51. Slot pipeline status renaming

UPDATE ENV_SLOT_PIPELINE
SET STATUS = 'CANDIDATE'
WHERE STATUS = 'ONGOING';

UPDATE ENV_SLOT_PIPELINE
SET STATUS = 'RUNNING'
WHERE STATUS = 'DEPLOYING';

UPDATE ENV_SLOT_PIPELINE
SET STATUS = 'DONE'
WHERE STATUS = 'DEPLOYED';

UPDATE ENV_SLOT_PIPELINE_CHANGE
SET STATUS = 'CANDIDATE'
WHERE STATUS = 'ONGOING';

UPDATE ENV_SLOT_PIPELINE_CHANGE
SET STATUS = 'RUNNING'
WHERE STATUS = 'DEPLOYING';

UPDATE ENV_SLOT_PIPELINE_CHANGE
SET STATUS = 'DONE'
WHERE STATUS = 'DEPLOYED';

UPDATE ENV_SLOT_WORKFLOWS
SET TRIGGER = 'CANDIDATE'
WHERE TRIGGER = 'CREATION';

UPDATE ENV_SLOT_WORKFLOWS
SET TRIGGER = 'RUNNING'
WHERE TRIGGER = 'DEPLOYING';

UPDATE ENV_SLOT_WORKFLOWS
SET TRIGGER = 'DONE'
WHERE TRIGGER = 'DEPLOYED';
