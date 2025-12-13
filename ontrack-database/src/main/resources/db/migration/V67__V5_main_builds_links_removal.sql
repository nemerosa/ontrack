-- 67. Removal of net.nemerosa.ontrack.extension.general.MainBuildLinksProjectPropertyType property

DELETE
FROM PROPERTIES
WHERE TYPE = 'net.nemerosa.ontrack.extension.general.MainBuildLinksProjectPropertyType';

DELETE
FROM SETTINGS
WHERE CATEGORY = 'net.nemerosa.ontrack.model.labels.MainBuildLinksConfig';
