package net.nemerosa.ontrack.extension.github.model

import net.nemerosa.ontrack.model.exceptions.InputException

class GitHubEngineConfigurationUserRequiredWithPasswordException: InputException(
    "The user field is required when the password is filled in."
)

class GitHubEngineConfigurationTokenMustBeVoidWithPasswordException: InputException(
    "The OAuth2 token must be blank when the password is filled in."
)

class GitHubEngineConfigurationAppMustBeVoidWithPasswordException: InputException(
    "The GitHub app fields must be blank when the password is filled in."
)

class GitHubEngineConfigurationAppMustBeVoidWithTokenException: InputException(
    "The GitHub app fields must be blank when the OAUth2 token is filled in."
)

class GitHubEngineConfigurationAppPrivateKeyRequiredException: InputException(
    "The GitHub app private key is required."
)

class GitHubEngineConfigurationIncorrectAppPrivateKeyException(msg: String): InputException(
    "Incorrect GitHub app private key: $msg."
)
