package net.nemerosa.ontrack.extension.git.support

import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.NoConfig
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GitCommitPropertyCommitLinkTest {

    private GitCommitPropertyCommitLink link
    private PropertyService propertyService

    @Before
    void before() {
        propertyService = mock(PropertyService)
        link = new GitCommitPropertyCommitLink(
                propertyService
        )
    }

    @Test(expected = NoGitCommitPropertyException)
    void 'Commit from build without property'() {
        def build = Build.of(
                Branch.of(
                        Project.of(
                                nd('P', '')
                        ),
                        nd('B', '')
                ),
                nd('1', ''),
                Signature.of('test')
        )
        when(propertyService.getProperty(build, GitCommitPropertyType)).thenReturn(
                Property.of(
                        new GitCommitPropertyType(),
                        null
                )
        )
        link.getCommitFromBuild(
                build,
                NoConfig.INSTANCE
        )
    }

    @Test
    void 'Commit from build with property'() {
        def build = Build.of(
                Branch.of(
                        Project.of(
                                nd('P', '')
                        ),
                        nd('B', '')
                ),
                nd('1', ''),
                Signature.of('test')
        )
        when(propertyService.getProperty(build, GitCommitPropertyType)).thenReturn(
                Property.of(
                        new GitCommitPropertyType(),
                        new GitCommitProperty("abcdef1")
                )
        )
        assert link.getCommitFromBuild(
                build,
                NoConfig.INSTANCE
        ) == "abcdef1"
    }

}
