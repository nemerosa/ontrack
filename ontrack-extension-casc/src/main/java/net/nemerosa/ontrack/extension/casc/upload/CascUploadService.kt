package net.nemerosa.ontrack.extension.casc.upload

interface CascUploadService {

    /**
     * Uploads some YAML content
     */
    fun upload(yaml: String)

    /**
     * Downlaods the uploaded content
     */
    fun download(): String?

}