package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SonarQubeMeasuresCollectionServiceImpl: SonarQubeMeasuresCollectionService {

    override fun collect(build: Build, property: SonarQubeProperty) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}