package net.nemerosa.ontrack.extension.casc.upload

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CascUploadServiceImpl(
    private val securityService: SecurityService,
    private val storageService: StorageService,
) : CascUploadService {

    override fun upload(yaml: String) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        storageService.store(STORE, KEY, yaml)
    }

    override fun download(): String? {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return storageService.findJson(STORE, KEY)?.asText()
    }

    companion object {
        private val STORE = CascUploadServiceImpl::class.java.name
        private const val KEY = "default"
    }
}