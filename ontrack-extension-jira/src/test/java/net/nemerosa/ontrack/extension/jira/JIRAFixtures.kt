package net.nemerosa.ontrack.extension.jira

object JIRAFixtures {

    fun jiraConfiguration(
        user: String? = "user",
        password: String? = "secret",
        include: List<String> = emptyList(),
        exclude: List<String> = emptyList(),
    ) =
        JIRAConfiguration(
            name = "test",
            url = "http://jira",
            user = user,
            password = password,
            include = include,
            exclude = exclude,
        )

}