# Mocking REST template clients

When a service uses a `RestTemplate` for its communication with an external service, the communication
must be simulated ("mocked") to allow for testing.

Example:

```kotlin
val client = ArtifactoryClientImpl(restTemplate)
```

In this case: how do we simulate the interactions of the `RestTemplate`:

```kotlin
@Test
fun buildNumbers() {
    val restTemplate = RestTemplate()
    val server = MockRestServiceServer.bindTo(restTemplate).build()

    val client = ArtifactoryClientImpl(restTemplate)

    server.expect(once(), requestTo("/api/build/PROJECT")).andExpect(method(HttpMethod.GET))
        .andRespond(
            withSuccess(
                mapOf(
                    "buildsNumbers" to listOf(
                        mapOf(
                            "uri" to "/1"
                        ),
                        mapOf(
                            "uri" to "/2"
                        )
                    )
                ).asJson().asJsonString(),
                MediaType.APPLICATION_JSON
            )
        )

    assertEquals(
        listOf("1", "2"),
        client.getBuildNumbers("PROJECT")
    )

    server.verify()
}
```

> NOTE: while this is good enough for unit tests, when doing more complex scenarios, it may be useful
> to use the `MockRestTemplateProvider` component in an integration test (see `JiraLinkNotificationChannelIT` for
> an example).
