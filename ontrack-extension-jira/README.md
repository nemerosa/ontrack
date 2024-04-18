# Jira extension

## Local testing

Automated tests are done using mocking of the Jira API.

However, if the needs arises to test the API against a _real
instance_ of Jira, you can follow this approach.

Launch a Jira local instance using Docker:

```bash
docker run -v \
   jiraVolume:/var/atlassian/application-data/jira \
   -d \
   -p 8080:8080 \
   atlassian/jira-software
```
This launches a Jira instance at http://localhost:8080/

> This takes quite a while to be ready...

Once started, follow the instructions and request an evaluation license.

> This license will be set up automatically.

For the _Administrator account setup_, enter the following parameters:

* email: any
* username: `admin`
* password: `admin`

Create a `TEST` project ("Basic software development" as a template).

You can now run any test class annotated with `@TestOnJiraServer`
as long as the `ONTRACK_TEST_EXTENSION_JIRA_ENABLED` environment
variable is set to `true`.

> See the  `JiraClientITUtils.kt` file for other options.
