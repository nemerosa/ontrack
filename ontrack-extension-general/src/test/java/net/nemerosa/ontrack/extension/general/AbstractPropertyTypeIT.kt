package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType

abstract class AbstractPropertyTypeIT : AbstractDSLTestSupport() {

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

    protected final inline fun <P, reified T : PropertyType<P>> ProjectEntity.setProperty(p: P) {
        setProperty(
                this,
                T::class.java,
                p
        )
    }

}