-- 22. Moving auto promotion level / validation stamp properties  (#325)

UPDATE PROPERTIES
SET TYPE = 'net.nemerosa.ontrack.extension.general.AutoValidationStampPropertyType'
WHERE TYPE = 'net.nemerosa.ontrack.boot.properties.AutoValidationStampPropertyType';

UPDATE PROPERTIES
SET TYPE = 'net.nemerosa.ontrack.extension.general.AutoPromotionLevelPropertyType'
WHERE TYPE = 'net.nemerosa.ontrack.boot.properties.AutoPromotionLevelPropertyType';
