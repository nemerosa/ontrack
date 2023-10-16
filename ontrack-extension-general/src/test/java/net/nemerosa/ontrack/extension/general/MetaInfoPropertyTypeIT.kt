package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MetaInfoPropertyTypeIT : AbstractDSLTestSupport() {

    @Test
    fun `Search on tokens`() {
        project {
            branch {
                val oldNameAndCategory = build("oldNameAndCategory") {
                    metaInfoProperty(this, metaInfoItem("name", "1.0.0", category = "category"))
                }
                val nameAndCategory = build("nameAndCategory") {
                    metaInfoProperty(this, metaInfoItem("name", "1.1.0", category = "category"))
                }
                val nameAndCategorySameValue = build("nameAndCategorySameValue") {
                    metaInfoProperty(this, metaInfoItem("name", "1.2.0", category = "category"))
                }
                val nameOnly = build("nameOnly") {
                    metaInfoProperty(this, metaInfoItem("name", "1.2.0", category = null))
                }
                val nameOnlyOtherValue = build("nameOnlyOtherValue") {
                    metaInfoProperty(this, metaInfoItem("name", "1.3.0", category = null))
                }
                build("other") {
                    metaInfoProperty(this, metaInfoItem("other", "1.0.0", category = null))
                }

                doSearch("Name only", "name", nameOnlyOtherValue, nameOnly, nameAndCategorySameValue, nameAndCategory, oldNameAndCategory)
                doSearch("Name and no category", "/name", nameOnlyOtherValue, nameOnly)
                doSearch("Name and category", "category/name", nameAndCategorySameValue, nameAndCategory, oldNameAndCategory)

                doSearch("Name and value", "name: 1.2.0", nameOnly, nameAndCategorySameValue)
                doSearch("Name and no category and value", "/name:1.2.0", nameOnly)
                doSearch("Name and no category and value pattern", "/name:1.*", nameOnlyOtherValue, nameOnly)
                doSearch("Name and category and value", "category/name:1.1.0", nameAndCategory)
                doSearch("Name and category and value pattern", "category/name:1*", nameAndCategorySameValue, nameAndCategory, oldNameAndCategory)
            }
        }
    }

    private fun Branch.doSearch(
        message: String,
        token: String,
        vararg expectedBuilds: Build,
    ) {
        val builds = structureService.buildSearch(
            project.id,
            BuildSearchForm(
                maximumCount = 10,
                property = MetaInfoPropertyType::class.java.name,
                propertyValue = token,
            )
        )
        assertEquals(
            expectedBuilds.toList().map(Build::name),
            builds.map(Build::name),
            message
        )
    }

}