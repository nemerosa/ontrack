package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.LabelProvider
import net.nemerosa.ontrack.model.labels.LabelProviderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LabelProviderServiceImpl(
        providers: List<LabelProvider>
) : LabelProviderService {

    private val index: Map<String, LabelProvider> = providers.associateBy { it::class.java.name }

    override fun getLabelProvider(id: String): LabelProvider? = index[id]

}