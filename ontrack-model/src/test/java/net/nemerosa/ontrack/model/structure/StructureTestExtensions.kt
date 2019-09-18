package net.nemerosa.ontrack.model.structure

fun createProject() = Project(
        ID.of(1),
        "PRJ",
        "Test project",
        false,
        Signature.of("test")
)

fun createBranch() = Branch(
        ID.of(10),
        "release-1.0",
        "Branch 1.0",
        false,
        BranchType.CLASSIC,
        createProject(),
        Signature.of("test")
)

fun createBuild() = Build(
        ID.of(1000),
        "1.0.0",
        "Build 1.0.0",
        Signature.of("test"),
        createBranch()
)
