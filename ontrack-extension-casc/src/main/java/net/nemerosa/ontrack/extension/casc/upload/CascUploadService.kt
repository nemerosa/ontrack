package net.nemerosa.ontrack.extension.casc.upload

interface CascUploadService {

    /**
     * Uploads some YAML content
     */
    fun upload(yaml: String)

}