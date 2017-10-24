package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.extension.api.support.*
import net.nemerosa.ontrack.model.structure.NameDescription
import org.junit.Test

class BranchQLIT extends AbstractQLITSupport {

    @Test
    void 'Branch by ID'() {
        def branch = doCreateBranch()

        def data = run("""{branches (id: ${branch.id}) { name } }""")
        assert data.branches.name == [branch.name]
    }

    @Test(expected = IllegalStateException)
    void 'Branch by ID and project is not allowed'() {
        run("""{branches (id: 1, project: "test") { name } }""")
    }

    @Test
    void 'Branch by project'() {
        def project = doCreateProject()
        doCreateBranch(project, NameDescription.nd("B1", ""))
        doCreateBranch(project, NameDescription.nd("B2", ""))

        def data = run("""{branches (project: "${project.name}") { name } }""")
        assert data.branches.name == ['B2', 'B1']
    }

    @Test
    void 'Branch by project and name'() {
        def project = doCreateProject()
        doCreateBranch(project, NameDescription.nd("B1", ""))
        doCreateBranch(project, NameDescription.nd("B2", ""))
        doCreateBranch(project, NameDescription.nd("C1", ""))

        def data = run("""{branches (project: "${project.name}", name: "C.*") { name } }""")
        assert data.branches.name == ['C1']
    }

    @Test
    void 'Branch by name'() {
        def p1 = doCreateProject()
        def b1 = doCreateBranch(p1, NameDescription.nd("B1", ""))
        doCreateBranch(p1, NameDescription.nd("B2", ""))
        def p2 = doCreateProject()
        def b2 = doCreateBranch(p2, NameDescription.nd("B1", ""))

        def data = run("""{branches (name: "B1") { id } }""")
        assert data.branches.id == [b1.id(), b2.id()]
    }

    @Test
    void 'Branch signature'() {
        def branch = doCreateBranch()
        def data = run("""{branches (id: ${branch.id}) { creation { user time } } }""")
        assert data.branches.first().creation.user == 'user'
        assert data.branches.first().creation.time.charAt(10) == 'T'
    }

    @Test
    void 'Branch without decorations'() {
        def branch = doCreateBranch()
        def data = run("""{
                branches (id: ${branch.id}) {
                    decorations {
                        decorationType
                        data
                        error
                    }
                }   
            }""")
        def decorations = data.branches.first().decorations
        assert decorations != null
        assert decorations.empty
    }

    @Test
    void 'Branch with decorations'() {
        def branch = doCreateBranch()
        setProperty branch, TestDecoratorPropertyType, new TestDecorationData("XXX", true)

        def data = run("""{
                branches (id: ${branch.id}) {
                    decorations {
                        decorationType
                        data
                        error
                    }
                }   
            }""")

        def decorations = data.branches.first().decorations
        assert decorations != null
        assert decorations.size() == 1
        def decoration = decorations.first()
        assert decoration.decorationType == TestDecorator.class.name
        assert decoration.data.value.asText() == 'XXX'

    }

    @Test
    void 'Branch with filtered decorations and match'() {
        def branch = doCreateBranch()
        setProperty branch, TestDecoratorPropertyType, new TestDecorationData("XXX", true)

        def data = run("""{
                branches (id: ${branch.id}) {
                    decorations(type: "${TestDecorator.class.name}") {
                        decorationType
                        data
                        error
                    }
                }   
            }""")

        def decorations = data.branches.first().decorations
        assert decorations != null
        assert decorations.size() == 1
        def decoration = decorations.first()
        assert decoration.decorationType == TestDecorator.class.name
        assert decoration.data.value.asText() == 'XXX'

    }

    @Test
    void 'Branch with filtered decorations and no match'() {
        def branch = doCreateBranch()
        setProperty branch, TestDecoratorPropertyType, new TestDecorationData("XXX", true)

        def data = run("""{
                branches (id: ${branch.id}) {
                    decorations(type: "unknown.Decorator") {
                        decorationType
                        data
                        error
                    }
                }   
            }""")

        def decorations = data.branches.first().decorations
        assert decorations != null
        assert decorations.empty
    }

    @Test
    void 'Promotion level branch reference'() {
        def pl = doCreatePromotionLevel()
        def data = run("""{
            branches (id: ${pl.branch.id}) {
                promotionLevels {
                    branch {
                        id
                        project {
                            id
                        }
                    }
                }
            }
        }""")
        def p = data.branches.first().promotionLevels.first()
        assert p.branch.id == pl.branch.id()
        assert p.branch.project.id == pl.branch.project.id()
    }

    @Test
    void 'Validation stamp branch reference'() {
        def vs = doCreateValidationStamp()
        def data = run("""{
            branches (id: ${vs.branch.id}) {
                validationStamps {
                    branch {
                        id
                        project {
                            id
                        }
                    }
                }
            }
        }""")
        def v = data.branches.first().validationStamps.first()
        assert v.branch.id == vs.branch.id()
        assert v.branch.project.id == vs.branch.project.id()
    }

    @Test
    void 'Branches filtered by property type'() {
        // Branches
        def p1 = doCreateBranch()
        /*def p2 = */ doCreateBranch()
        def p3 = doCreateBranch()
        def p4 = doCreateBranch()
        // Properties
        setProperty(p1, TestSimplePropertyType, new TestSimpleProperty("P1"))
        setProperty(p3, TestSimplePropertyType, new TestSimpleProperty("P3"))
        setProperty(p4, TestSimplePropertyType, new TestSimpleProperty("X1"))
        // Looks for projects having this property
        def data = run("""{
            branches(withProperty: {type: "${TestSimplePropertyType.class.name}"}) {
                name
            }
        }""")
        assert data.branches*.name as Set == [p1.name, p3.name, p4.name] as Set
    }

    @Test
    void 'Branches filtered by property type and value pattern'() {
        // Branches
        def p1 = doCreateBranch()
        /*def p2 = */ doCreateBranch()
        def p3 = doCreateBranch()
        def p4 = doCreateBranch()
        // Properties
        setProperty(p1, TestSimplePropertyType, new TestSimpleProperty("P1"))
        setProperty(p3, TestSimplePropertyType, new TestSimpleProperty("P3"))
        setProperty(p4, TestSimplePropertyType, new TestSimpleProperty("X1"))
        // Looks for projects having this property
        def data = run("""{
            branches(withProperty: {type: "${TestSimplePropertyType.class.name}", value: "P"}) {
                name
            }
        }""")
        assert data.branches*.name as Set == [p1.name, p3.name] as Set
    }

    @Test
    void 'Branches filtered by property type and value'() {
        // Branches
        def p1 = doCreateBranch()
        /*def p2 = */ doCreateBranch()
        def p3 = doCreateBranch()
        def p4 = doCreateBranch()
        // Properties
        setProperty(p1, TestSimplePropertyType, new TestSimpleProperty("P1"))
        setProperty(p3, TestSimplePropertyType, new TestSimpleProperty("P3"))
        setProperty(p4, TestSimplePropertyType, new TestSimpleProperty("X1"))
        // Looks for projects having this property
        def data = run("""{
            branches(withProperty: {type: "${TestSimplePropertyType.class.name}", value: "P1"}) {
                name
            }
        }""")
        assert data.branches*.name as Set == [p1.name] as Set
    }

}
