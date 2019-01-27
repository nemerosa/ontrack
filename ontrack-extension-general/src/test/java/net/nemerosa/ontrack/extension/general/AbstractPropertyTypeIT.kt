package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.NameValue

abstract class AbstractPropertyTypeIT : AbstractDSLTestSupport() {

    protected fun ProjectEntity.links(vararg data: Pair<String, String>) {
        setProperty(
                this,
                LinkPropertyType::class.java,
                LinkProperty(
                        data.map {
                            NameValue(it.first, it.second)
                        }
                )
        )
    }

    protected fun ProjectEntity.metaInfo(vararg data: Pair<String, String>) {
        setProperty(
                this,
                MetaInfoPropertyType::class.java,
                MetaInfoProperty(
                        data.map {
                            MetaInfoPropertyItem(it.first, it.second, null, null)
                        }
                )
        )
    }

    protected fun ProjectEntity.release(name: String) {
        setProperty(
                this,
                ReleasePropertyType::class.java,
                ReleaseProperty(name)
        )
    }

    protected fun ProjectEntity.message(text: String, type: MessageType = MessageType.INFO) {
        setProperty(
                this,
                MessagePropertyType::class.java,
                MessageProperty(type, text)
        )
    }

}