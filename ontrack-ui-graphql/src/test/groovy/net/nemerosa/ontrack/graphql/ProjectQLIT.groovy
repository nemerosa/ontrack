package net.nemerosa.ontrack.graphql

import graphql.GraphQLException
import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.security.PromotionRunCreate
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test

class ProjectQLIT extends AbstractQLITSupport {

    @Test
    void 'All projects'() {
        def p = doCreateProject()
        def data = run('{projects { id name }}')
        assert data.projects*.name.contains(p.name)
        assert data.projects*.id.contains(p.id())
    }

    @Test
    void 'Project by ID'() {
        def p = doCreateProject()
        def data = run("{projects(id: ${p.id}) { name }}")
        assert data.projects.first().name == p.name
    }

    @Test(expected = GraphQLException)
    void 'Project by ID and name is not authorised'() {
        run("""{projects(id: 1, name: "test") { name }}""")
    }

    @Test
    void 'Project by name'() {
        def p = doCreateProject()
        def data = run("""{projects(name: "${p.name}") { id }}""")
        assert data.projects.first().id == p.id()
    }

    @Test
    void 'Project branches'() {
        def p = doCreateProject()
        doCreateBranch(p, NameDescription.nd("B1", ""))
        doCreateBranch(p, NameDescription.nd("B2", ""))
        def data = run("{projects(id: ${p.id}) { name branches { name } }}")
        assert data.projects.branches.name.flatten() == ["B2", "B1"]
    }

    @Test
    void 'Branch by name'() {
        def p = doCreateProject()
        doCreateBranch(p, NameDescription.nd("B1", ""))
        doCreateBranch(p, NameDescription.nd("B2", ""))

        def data = run("""{projects(id: ${p.id}) { name branches(name: "B2") { name } } }""")
        assert data.projects.branches.name.flatten() == ["B2"]
    }

    @Test
    void 'Branch by regular expression'() {
        def p = doCreateProject()
        doCreateBranch(p, NameDescription.nd("11.8.3", ""))
        doCreateBranch(p, NameDescription.nd("11.9.0", ""))

        def data = run("""{projects(id: ${p.id}) { name branches(name: "11\\\\.9.*") { name } } }""")
        assert data.projects.branches.name.flatten() == ["11.9.0"]
    }

    @Test
    void 'Promotion levels for a branch'() {
        def branch = doCreateBranch()
        def project = branch.project
        (1..5).each {
            doCreatePromotionLevel(branch, NameDescription.nd("PL${it}", "Promotion level ${it}"))
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    promotionLevels {
                        name
                    }
                }
            }
        }""")
        assert data.projects.branches.promotionLevels.name.flatten() == (1..5).collect { "PL${it}" }
    }

    @Test
    void 'Validation stamps for a branch'() {
        def branch = doCreateBranch()
        def project = branch.project
        (1..5).each {
            doCreateValidationStamp(branch, NameDescription.nd("VS${it}", "Validation stamp ${it}"))
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    validationStamps {
                        name
                    }
                }
            }
        }""")
        assert data.projects.branches.validationStamps.name.flatten() == (1..5).collect { "VS${it}" }
    }

    @Test
    void 'Promotion runs for a promotion level'() {
        def pl = doCreatePromotionLevel()
        def branch = pl.branch
        def project = branch.project
        (1..5).each {
            def build = doCreateBuild(branch, NameDescription.nd("${it}", "Build ${it}"))
            if (it % 2 == 0) {
                asUser().with(project, PromotionRunCreate).call {
                    structureService.newPromotionRun(
                            PromotionRun.of(
                                    build,
                                    pl,
                                    Signature.of('test'),
                                    "Promotion"
                            )
                    )
                }
            }
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    promotionLevels {
                        name
                        promotionRuns {
                            edges {
                                node {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.projects.branches.promotionLevels.promotionRuns.edges.node.build.name.flatten() == ['4', '2']
    }

    @Test
    void 'Validation runs for a validation stamp'() {
        def vs = doCreateValidationStamp()
        def branch = vs.branch
        def project = branch.project
        (1..5).each {
            def build = doCreateBuild(branch, NameDescription.nd("${it}", "Build ${it}"))
            if (it % 2 == 0) {
                asUser().with(project, ValidationRunCreate).call {
                    structureService.newValidationRun(
                            ValidationRun.of(
                                    build,
                                    vs,
                                    0,
                                    Signature.of('test'),
                                    ValidationRunStatusID.STATUS_PASSED,
                                    "Validation"
                            )
                    )
                }
            }
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    validationStamps {
                        name
                        validationRuns {
                            edges {
                                node {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.projects.branches.validationStamps.validationRuns.edges.node.build.name.flatten() == ['4', '2']
    }

    @Test
    void 'Validation run statuses for a run for a validation stamp'() {
        def vs = doCreateValidationStamp()
        def branch = vs.branch
        def project = branch.project
        def build = doCreateBuild(branch, NameDescription.nd("1", "Build 1"))
        def validationRun = asUser().with(project, ValidationRunCreate).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            0,
                            Signature.of('test'),
                            ValidationRunStatusID.STATUS_FAILED,
                            "Validation failed"
                    )
            )
        }
        asUser().with(project, ValidationRunStatusChange).call {
            structureService.newValidationRunStatus(
                    validationRun,
                    ValidationRunStatus.of(
                            Signature.of('test'),
                            ValidationRunStatusID.STATUS_INVESTIGATING,
                            "Investigating"
                    )
            )
            structureService.newValidationRunStatus(
                    validationRun,
                    ValidationRunStatus.of(
                            Signature.of('test'),
                            ValidationRunStatusID.STATUS_EXPLAINED,
                            "Explained"
                    )
            )
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    validationStamps {
                        name
                        validationRuns {
                            edges {
                                node {
                                    validationRunStatuses {
                                        statusID {
                                            id
                                        }
                                        description
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }""")
        def validationRunStatuses = data.projects.branches.validationStamps.validationRuns.edges.node.validationRunStatuses.flatten()
        assert validationRunStatuses.statusID.id as Set == ['EXPLAINED', 'INVESTIGATING', 'FAILED'] as Set
        assert validationRunStatuses.description as Set == ['Explained', 'Investigating', 'Validation failed'] as Set
    }

    @Test
    void 'Promotion runs for a build'() {
        def branch = doCreateBranch()
        def pl1 = doCreatePromotionLevel(branch, NameDescription.nd("PL1", ""))
        def pl2 = doCreatePromotionLevel(branch, NameDescription.nd("PL2", ""))
        def project = branch.project
        def build = doCreateBuild(branch, NameDescription.nd("1", ""))
        asUser().with(project, PromotionRunCreate).call {
            structureService.newPromotionRun(
                    PromotionRun.of(
                            build,
                            pl1,
                            Signature.of('test'),
                            "Promotion 1"
                    )
            )
            structureService.newPromotionRun(
                    PromotionRun.of(
                            build,
                            pl2,
                            Signature.of('test'),
                            "Promotion 2"
                    )
            )
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    builds(count: 1) {
                        promotionRuns {
                            promotionLevel {
                                name
                            }
                        }
                    }
                }
            }
        }""")
        assert data.projects.branches.builds.promotionRuns.promotionLevel.name.flatten() as Set == ['PL2', 'PL1'] as Set
    }

    @Test
    void 'Filtered promotion runs for a build'() {
        def branch = doCreateBranch()
        def pl1 = doCreatePromotionLevel(branch, NameDescription.nd("PL1", ""))
        def pl2 = doCreatePromotionLevel(branch, NameDescription.nd("PL2", ""))
        def project = branch.project
        def build = doCreateBuild(branch, NameDescription.nd("1", ""))
        asUser().with(project, PromotionRunCreate).call {
            structureService.newPromotionRun(
                    PromotionRun.of(
                            build,
                            pl1,
                            Signature.of('test'),
                            "Promotion 1"
                    )
            )
            structureService.newPromotionRun(
                    PromotionRun.of(
                            build,
                            pl2,
                            Signature.of('test'),
                            "Promotion 2"
                    )
            )
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    builds(count: 1) {
                        promotionRuns(promotion: "PL1") {
                            promotionLevel {
                                name
                            }
                        }
                    }
                }
            }
        }""")
        assert data.projects.branches.builds.promotionRuns.promotionLevel.name.flatten() == ['PL1']
    }

    @Test
    void 'Filtered list of promotion runs for a promotion level'() {
        def pl = doCreatePromotionLevel()
        def branch = pl.branch
        def project = branch.project
        (1..20).each {
            def build = doCreateBuild(branch, NameDescription.nd("${it}", "Build ${it}"))
            if (it % 2 == 0) {
                asUser().with(project, PromotionRunCreate).call {
                    structureService.newPromotionRun(
                            PromotionRun.of(
                                    build,
                                    pl,
                                    Signature.of('test'),
                                    "Promotion"
                            )
                    )
                }
            }
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    promotionLevels {
                        name
                        promotionRuns(first: 5) {
                            edges {
                                node {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.projects.branches.promotionLevels.promotionRuns.edges.node.build.name.flatten() == ['20', '18', '16', '14', '12']
    }

    @Test
    void 'Builds for a branch'() {
        def b = doCreateBuild()
        def p = b.project
        def branchName = b.branch.name
        def data = run("""{
            projects(id: ${p.id}) {
                name
                branches(name: "${branchName}") {
                    name
                    builds {
                        name
                    }
                }
            }
        }""")
        assert data.projects.branches.builds.name.flatten() == [b.name]
    }

    @Test
    void 'Build edge query for a branch'() {
        def branch = doCreateBranch()
        def project = branch.project
        (1..20).each {
            doCreateBuild(branch, NameDescription.nd("${it}", "Build ${it}"))
        }
        def data = run("""{
            projects(id: ${project.id}) {
                name
                branches(name: "${branch.name}") {
                    name
                    builds(count: 5) {
                        name
                    }
                }
            }
        }""")
        assert data.projects.branches.builds.name.flatten() == ['20', '19', '18', '17', '16']
    }

    @Test
    void 'Projects filtered by property type'() {
        // Projects
        def p1 = doCreateProject()
        /*def p2 = */doCreateProject()
        def p3 = doCreateProject()
        def p4 = doCreateProject()
        // Properties
        setProperty(p1, TestSimplePropertyType, new TestSimpleProperty("P1"))
        setProperty(p3, TestSimplePropertyType, new TestSimpleProperty("P3"))
        setProperty(p4, TestSimplePropertyType, new TestSimpleProperty("X1"))
        // Looks for projects having this property
        def data = run("""{
            projects(withProperty: {type: "${TestSimplePropertyType.class.name}"}) {
                name
            }
        }""")
        assert data.projects*.name as Set == [p1.name, p3.name, p4.name] as Set
    }

    @Test
    void 'Projects filtered by property type and value pattern'() {
        // Projects
        def p1 = doCreateProject()
        /*def p2 = */doCreateProject()
        def p3 = doCreateProject()
        def p4 = doCreateProject()
        // Properties
        setProperty(p1, TestSimplePropertyType, new TestSimpleProperty("P1"))
        setProperty(p3, TestSimplePropertyType, new TestSimpleProperty("P3"))
        setProperty(p4, TestSimplePropertyType, new TestSimpleProperty("X1"))
        // Looks for projects having this property
        def data = run("""{
            projects(withProperty: {type: "${TestSimplePropertyType.class.name}", value: "P"}) {
                name
            }
        }""")
        assert data.projects*.name as Set == [p1.name, p3.name] as Set
    }

    @Test
    void 'Projects filtered by property type and value'() {
        // Projects
        def p1 = doCreateProject()
        /*def p2 = */doCreateProject()
        def p3 = doCreateProject()
        def p4 = doCreateProject()
        // Properties
        setProperty(p1, TestSimplePropertyType, new TestSimpleProperty("P1"))
        setProperty(p3, TestSimplePropertyType, new TestSimpleProperty("P3"))
        setProperty(p4, TestSimplePropertyType, new TestSimpleProperty("X1"))
        // Looks for projects having this property
        def data = run("""{
            projects(withProperty: {type: "${TestSimplePropertyType.class.name}", value: "P1"}) {
                name
            }
        }""")
        assert data.projects*.name as Set == [p1.name] as Set
    }

}
