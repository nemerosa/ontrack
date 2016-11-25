package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Signature
import org.junit.Test

import java.time.LocalDateTime

class BuildQLIT extends AbstractQLITSupport {

    @Test
    void 'Build creation'() {
        def branch = doCreateBranch()
        def build = asUser().with(branch, BuildCreate).call {
            structureService.newBuild(
                    Build.of(
                            branch,
                            NameDescription.nd('1', ''),
                            Signature.of(
                                    LocalDateTime.of(2016, 11, 25, 14, 43),
                                    "test"
                            )
                    )
            )
        }

        def data = run("""{builds (id: ${build.id}) { creation { user time } } }""")
        assert data.builds.first().creation.user == 'test'
        assert data.builds.first().creation.time == '2016-11-25T14:43:00'
    }

}
