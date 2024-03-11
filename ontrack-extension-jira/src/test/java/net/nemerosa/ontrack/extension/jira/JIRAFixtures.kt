package net.nemerosa.ontrack.extension.jira

object JIRAFixtures {

    fun jiraConfiguration(
        include: List<String> = emptyList(),
        exclude: List<String> = emptyList(),
    ) =
        JIRAConfiguration(
            name = "test",
            url = "http://jira",
            user = "user",
            password = "secret",
            include = include,
            exclude = exclude,
        )

}