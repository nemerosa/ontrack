package net.nemerosa.ontrack.extension.sonarqube.measures

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.extension.casc.CascExtensionFeature
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.sonarqube.SonarQubeExtensionFeature
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.createBranch
import net.nemerosa.ontrack.model.structure.createBuild
import net.nemerosa.ontrack.test.assertIs
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SonarQubeMeasuresInformationExtensionTest {

    private lateinit var sonarQubeMeasuresCollectionService: SonarQubeMeasuresCollectionService
    private lateinit var informationExtension: SonarQubeMeasuresInformationExtension

    @Before
    fun setup() {
        sonarQubeMeasuresCollectionService = mock()
        informationExtension = SonarQubeMeasuresInformationExtension(
                extensionFeature = SonarQubeExtensionFeature(IndicatorsExtensionFeature(), CascExtensionFeature()),
                sonarQubeMeasuresCollectionService = sonarQubeMeasuresCollectionService
        )
    }

    @Test
    fun `Getting measures for a build`() {
        val build: Build = createBuild()
        whenever(sonarQubeMeasuresCollectionService.getMeasures(build)).thenReturn(
                SonarQubeMeasures(
                        mapOf(
                                "measure-1" to 12.3,
                                "measure-2" to 20.0
                        )
                )
        )
        val info = informationExtension.getInformation(build)
        assertNotNull(info) {
            assertIs<SonarQubeMeasures>(it.data) { q ->
                assertEquals(
                        mapOf(
                                "measure-1" to 12.3,
                                "measure-2" to 20.0
                        ),
                        q.measures
                )
            }
        }
    }

    @Test
    fun `Not getting any measure when no measure is attached to the build`() {
        val build: Build = createBuild()
        val info = informationExtension.getInformation(build)
        assertNull(info)
    }

    @Test
    fun `Not getting any measure for something different than a build`() {
        val info = informationExtension.getInformation(createBranch())
        assertNull(info)
    }

}