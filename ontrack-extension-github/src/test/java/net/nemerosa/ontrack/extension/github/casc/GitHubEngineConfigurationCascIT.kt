package net.nemerosa.ontrack.extension.github.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GitHubEngineConfigurationCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var gitHubConfigurationService: GitHubConfigurationService

    @Autowired
    private lateinit var gitHubEngineConfigurationContext: GitHubEngineConfigurationContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = gitHubEngineConfigurationContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "items": {
                    "title": "GitHubEngineConfiguration",
                    "description": null,
                    "properties": {
                      "appId": {
                        "description": "appId field",
                        "type": "string"
                      },
                      "appInstallationAccountName": {
                        "description": "appInstallationAccountName field",
                        "type": "string"
                      },
                      "appPrivateKey": {
                        "description": "appPrivateKey field",
                        "type": "string"
                      },
                      "autoMergeToken": {
                        "description": "autoMergeToken field",
                        "type": "string"
                      },
                      "name": {
                        "description": "name field",
                        "type": "string"
                      },
                      "oauth2Token": {
                        "description": "oauth2Token field",
                        "type": "string"
                      },
                      "password": {
                        "description": "password field",
                        "type": "string"
                      },
                      "url": {
                        "description": "url field",
                        "type": "string"
                      },
                      "user": {
                        "description": "user field",
                        "type": "string"
                      }
                    },
                    "required": [
                      "name",
                      "url"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of GitHub configurations",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Defining a GitHub configuration using a user and password`() {
        val name = TestUtils.uid("GH")
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            github:
                                - name: $name
                                  user: my-user
                                  password: my-secret-password
                """.trimIndent()
            )
        }
        // Checks the GitHub configuration has been registered
        asAdmin {
            val configurations = gitHubConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://github.com", configuration.url)
            assertEquals("my-user", configuration.user)
            assertEquals("my-secret-password", configuration.password)
            assertEquals(null, configuration.oauth2Token)
            assertEquals(null, configuration.appId)
            assertEquals(null, configuration.appPrivateKey)
            assertEquals(null, configuration.appInstallationAccountName)
        }
    }

    @Test
    fun `Defining a GitHub configuration using a token`() {
        val name = TestUtils.uid("GH")
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            github:
                                - name: $name
                                  token: my-secret-token
                """.trimIndent()
            )
        }
        // Checks the GitHub configuration has been registered
        asAdmin {
            val configurations = gitHubConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://github.com", configuration.url)
            assertEquals(null, configuration.user)
            assertEquals(null, configuration.password)
            assertEquals("my-secret-token", configuration.oauth2Token)
            assertEquals(null, configuration.appId)
            assertEquals(null, configuration.appPrivateKey)
            assertEquals(null, configuration.appInstallationAccountName)
        }
    }

    @Test
    fun `Defining a GitHub configuration using an app`() {
        val name = TestUtils.uid("GH")
        // PKey content at /test-app.pem
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            github:
                                - name: $name
                                  app-id: 123456
                                  app-private-key: |
                                    -----BEGIN PRIVATE KEY-----
                                    MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDy6NM0KGZojJnJ
                                    6ssXpmMqoMcXIISbBqTas8+6EQF+wQ2AyJCajbL/jCJf4X0UzYH5SMZUDA2njGnq
                                    NeIo1SLXDQLBlZnWYYLN7UsxC/5X0p9EmVv+ve56KJob7IQtVhxzhq3pTSIjbYEi
                                    mNOZfhvGdzc+x8l1lgBi41l1ryOsCLTfxtcg1KFIKKjzb4OYLd4NnW2GeUmzpLgD
                                    4re/N5omdmQJJa2xftRx3xwUVjZrOO6EaNtpjfOP74Aawr8p6oNArhBKV8wpniYs
                                    zcnPoe7zWOF9E8wq/lXWMru2z3j0Zsa4jYEqf1r1hD4eJcoFhuf9dIeKaUp2V5LF
                                    +3aDaqExAgMBAAECggEAHpjDGbvJAnxLVsqzYDw+G3169mfLLDfPBhlGHhrMRGoO
                                    yFz7EeytUlEVd1xQxBEKwRqwmJ+YDLW8FZtJ7HjEVbA226l7YiyQLF4qbXgkRm2q
                                    UBq9Ir5LzqlUmBXXpFpHZSneuBdWKH4/mNsFND1VRE957vd9YclOLSaT+yXfaWjE
                                    hS2oUIhTiS0sovqFEkTmjgIFjGXFoQ/ymJxkVcqoyUp+TYK8gWdz061GvS3FTL1X
                                    9cw6rgWaEHxax0pqIhBsF5Wo03fdoKgAeiot74esjGf/Z8fYE3oAecP1oUFu6L5c
                                    7OZusg+65kErxDFoSZ9qqd6pc3F+9rua9M5qANy+AQKBgQD6mcGrIs7BwH2ZAvl6
                                    W0MG6aBRq8FSwECC3TNmNCPPOJ5+QS9o9NWZLFGCmSrks6FON0M9Rz1gROb0qPRn
                                    YKq3eM4xCTjRGpkJ+32x8AhM7rbmvyXI+cXStxMYTrsN2t5P3aWwZBtEbOi/hKIA
                                    k5Gt4vfCyv+X5AEB78cLZlsyIQKBgQD4JKV0fcU+OTfUTKdH40OR3RwKgYUDDOVh
                                    ksKj72ZT0L7yJ5G6i5onHDYqSm8wRVthUAZvs7gO3wnO1/Oy2iIc9/smREszPD8J
                                    FZlmvqWhxHvnNidTgIu3sXwCY10yf41vttvrbfbGDyoF1F6G9J5a172SmU0ngkmc
                                    008n/vytEQKBgQCslGjQf5cdzX9xeZ/viJv/TbMvq3XmlCmZNdao4u1qTtavohqR
                                    UFMtOl0j8HGesKo8oEg5Ei+NdcYL5bLy4pqO4a42DODI+GU+f6iPevtsZ9Uj0a2m
                                    24RF2fhXfBjLsNf67mylrjstA0fCZQfgF8BynOT9jCk7JDUhbUyJaEMToQKBgCT8
                                    EU0Tkdp8XL3fzu8ACIotEojeVRznRykL3sbgX7gOXOdqzmWneQprQgd7oKbpL32J
                                    l+v+NWjCBYw207PMn1kB/QTvGOZCDIMmHP8bW0SJLI6Bm8ruVeTDJ2CTvshQCpyj
                                    /JNSiH1stS65QH2M6C1SCodXIhDJcn9VX27uqmqBAoGBAMKNITf1divT4Wdk3bAq
                                    tYC3NlDSI5FfT0esnno9lpLgPcZhYbCJ93Jf5vfpcQ5t2pt29nxKQdVb85UWOJDv
                                    U2YcYmNPODjCpjUgKuDp2A2Bp/xUtafsR3AWjOjfKxDKSY3HxVsT09l1NNnoKQ6l
                                    vJ+5Pnp+vCydc7gA9CD3YNgE
                                    -----END PRIVATE KEY-----
        """.trimIndent()
            )
        }
        // Checks the GitHub configuration has been registered
        asAdmin {
            val configurations = gitHubConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://github.com", configuration.url)
            assertEquals(null, configuration.user)
            assertEquals(null, configuration.password)
            assertEquals(null, configuration.oauth2Token)
            assertEquals("123456", configuration.appId)
            assertEquals(TestUtils.resourceString("/test-app.pem"), configuration.appPrivateKey)
            assertEquals(null, configuration.appInstallationAccountName)
        }
    }

    @Test
    fun `Defining a GitHub configuration using an app and an installation`() {
        val name = TestUtils.uid("GH")
        // PKey content at /test-app.pem
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            github:
                                - name: $name
                                  app-id: 123456
                                  app-private-key: |
                                    -----BEGIN PRIVATE KEY-----
                                    MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDy6NM0KGZojJnJ
                                    6ssXpmMqoMcXIISbBqTas8+6EQF+wQ2AyJCajbL/jCJf4X0UzYH5SMZUDA2njGnq
                                    NeIo1SLXDQLBlZnWYYLN7UsxC/5X0p9EmVv+ve56KJob7IQtVhxzhq3pTSIjbYEi
                                    mNOZfhvGdzc+x8l1lgBi41l1ryOsCLTfxtcg1KFIKKjzb4OYLd4NnW2GeUmzpLgD
                                    4re/N5omdmQJJa2xftRx3xwUVjZrOO6EaNtpjfOP74Aawr8p6oNArhBKV8wpniYs
                                    zcnPoe7zWOF9E8wq/lXWMru2z3j0Zsa4jYEqf1r1hD4eJcoFhuf9dIeKaUp2V5LF
                                    +3aDaqExAgMBAAECggEAHpjDGbvJAnxLVsqzYDw+G3169mfLLDfPBhlGHhrMRGoO
                                    yFz7EeytUlEVd1xQxBEKwRqwmJ+YDLW8FZtJ7HjEVbA226l7YiyQLF4qbXgkRm2q
                                    UBq9Ir5LzqlUmBXXpFpHZSneuBdWKH4/mNsFND1VRE957vd9YclOLSaT+yXfaWjE
                                    hS2oUIhTiS0sovqFEkTmjgIFjGXFoQ/ymJxkVcqoyUp+TYK8gWdz061GvS3FTL1X
                                    9cw6rgWaEHxax0pqIhBsF5Wo03fdoKgAeiot74esjGf/Z8fYE3oAecP1oUFu6L5c
                                    7OZusg+65kErxDFoSZ9qqd6pc3F+9rua9M5qANy+AQKBgQD6mcGrIs7BwH2ZAvl6
                                    W0MG6aBRq8FSwECC3TNmNCPPOJ5+QS9o9NWZLFGCmSrks6FON0M9Rz1gROb0qPRn
                                    YKq3eM4xCTjRGpkJ+32x8AhM7rbmvyXI+cXStxMYTrsN2t5P3aWwZBtEbOi/hKIA
                                    k5Gt4vfCyv+X5AEB78cLZlsyIQKBgQD4JKV0fcU+OTfUTKdH40OR3RwKgYUDDOVh
                                    ksKj72ZT0L7yJ5G6i5onHDYqSm8wRVthUAZvs7gO3wnO1/Oy2iIc9/smREszPD8J
                                    FZlmvqWhxHvnNidTgIu3sXwCY10yf41vttvrbfbGDyoF1F6G9J5a172SmU0ngkmc
                                    008n/vytEQKBgQCslGjQf5cdzX9xeZ/viJv/TbMvq3XmlCmZNdao4u1qTtavohqR
                                    UFMtOl0j8HGesKo8oEg5Ei+NdcYL5bLy4pqO4a42DODI+GU+f6iPevtsZ9Uj0a2m
                                    24RF2fhXfBjLsNf67mylrjstA0fCZQfgF8BynOT9jCk7JDUhbUyJaEMToQKBgCT8
                                    EU0Tkdp8XL3fzu8ACIotEojeVRznRykL3sbgX7gOXOdqzmWneQprQgd7oKbpL32J
                                    l+v+NWjCBYw207PMn1kB/QTvGOZCDIMmHP8bW0SJLI6Bm8ruVeTDJ2CTvshQCpyj
                                    /JNSiH1stS65QH2M6C1SCodXIhDJcn9VX27uqmqBAoGBAMKNITf1divT4Wdk3bAq
                                    tYC3NlDSI5FfT0esnno9lpLgPcZhYbCJ93Jf5vfpcQ5t2pt29nxKQdVb85UWOJDv
                                    U2YcYmNPODjCpjUgKuDp2A2Bp/xUtafsR3AWjOjfKxDKSY3HxVsT09l1NNnoKQ6l
                                    vJ+5Pnp+vCydc7gA9CD3YNgE
                                    -----END PRIVATE KEY-----
                                  app-installation: nemerosa
        """.trimIndent()
            )
        }
        // Checks the GitHub configuration has been registered
        asAdmin {
            val configurations = gitHubConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://github.com", configuration.url)
            assertEquals(null, configuration.user)
            assertEquals(null, configuration.password)
            assertEquals(null, configuration.oauth2Token)
            assertEquals("123456", configuration.appId)
            assertEquals(TestUtils.resourceString("/test-app.pem").trim(), configuration.appPrivateKey?.trim())
            assertEquals("nemerosa", configuration.appInstallationAccountName)
        }
    }

}