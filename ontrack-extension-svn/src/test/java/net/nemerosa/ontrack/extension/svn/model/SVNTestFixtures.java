package net.nemerosa.ontrack.extension.svn.model;

public final class SVNTestFixtures {

    private SVNTestFixtures() {
    }

    public static SVNConfiguration configuration() {
        return new SVNConfiguration(
                "Name",
                "http://host/repository",
                "user",
                "secret",
                "",
                "http://browser/file/{path}",
                "http://browser/revision/{revision}",
                "http://browser/file/{path}/{revision}",
                0,
                1,
                ""
        );
    }
}
