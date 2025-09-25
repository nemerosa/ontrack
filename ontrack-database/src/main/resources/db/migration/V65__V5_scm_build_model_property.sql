-- 65. Migration of the branch model property from the Git to the Scm extension

UPDATE PROPERTIES
SET TYPE = 'net.nemerosa.ontrack.extension.scm.branching.BranchingModelPropertyType'
WHERE TYPE = 'net.nemerosa.ontrack.extension.git.branching.BranchingModelPropertyType';
