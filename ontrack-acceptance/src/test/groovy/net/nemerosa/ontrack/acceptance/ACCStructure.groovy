package net.nemerosa.ontrack.acceptance

import org.junit.Test

import static net.nemerosa.ontrack.json.JsonUtils.object

class ACCStructure extends AcceptanceTestClient {

    @Test
    void 'No name for a project is invalid'() {
        validationMessage({ doCreateProject(object().end()) }, "The name is required.")
    }

    @Test
    void 'Empty name for a project is invalid'() {
        validationMessage({
            doCreateProject(object().with('name', '').end())
        }, 'The name can only have letter, digits, dot (.), dashes (-) or underscores (_).')
    }

}
