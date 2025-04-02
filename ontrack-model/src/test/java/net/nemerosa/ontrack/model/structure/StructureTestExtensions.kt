package net.nemerosa.ontrack.model.structure

@Deprecated("Use ProjectFixtures")
fun createProject() = Project(
        ID.of(1),
        "PRJ",
        "Test project",
        false,
        Signature.of("test")
)

@Deprecated("Use BranchFixtures")
fun createBranch() = Branch(
        ID.of(10),
        "release-1.0",
        "Branch 1.0",
        false,
        createProject(),
        Signature.of("test")
)

@Deprecated("Use BuildFixtures")
fun createBuild() = Build(
        ID.of(1000),
        "1.0.0",
        "Build 1.0.0",
        Signature.of("test"),
        createBranch()
)
