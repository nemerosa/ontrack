package net.nemerosa.ontrack.extension.git.support

import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock

class GitCommitPropertyCommitLinkTest {

    private GitCommitPropertyCommitLink link
    private PropertyService propertyService
    private StructureService structureService

    @Before
    void before() {
        propertyService = mock(PropertyService)
        structureService = mock(StructureService)
        link = new GitCommitPropertyCommitLink(
                propertyService,
                structureService
        )
    }

    @Test(expected = NoGitCommitPropertyException)
    void 'Commit from build without property'() {
        link.getCommitFromBuild(
                Build.of(
                        Branch.of(
                                Project.of(
                                        nd('P', '')
                                ),
                                nd('B', '')
                        ),
                        nd('1', ''),
                        Signature.of('test')
                ),
                NoConfig.INSTANCE
        )
    }

}
