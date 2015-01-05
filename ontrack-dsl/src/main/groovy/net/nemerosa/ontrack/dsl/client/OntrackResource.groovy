package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.dsl.*

/**
 * Entry point for the DSL.
 */
class OntrackResource implements Ontrack, OntrackConnector {

    /**
     * JSON client
     */
    private final JsonClient jsonClient

    /**
     * Construction of the Ontrack client, based on a raw JSON client
     */
    OntrackResource(JsonClient jsonClient) {
        this.jsonClient = jsonClient
    }

    @Override
    Project project(String name) {
        // Gets the list of projects and looks for an existing project
        def projectNode = get("structure/projects").resources.find {
            it.name == name
        }
        // If project exists, loads it
        if (projectNode) {
            new ProjectResource(
                    this,
                    get(projectNode._self)
            )
        }
        // If it does not exist, creates it
        else {
            new ProjectResource(
                    this,
                    post(
                            'structure/projects/create',
                            [
                                    name       : name,
                                    description: ''
                            ]
                    )
            )
        }
    }

    @Override
    Project project(String name, Closure closure) {
        def project = project(name)
        project.call(closure)
        project
    }

    @Override
    Branch branch(String project, String branch) {
        new BranchResource(
                this,
                get("structure/entity/branch/${project}/${branch}")
        )
    }

    @Override
    PromotionLevel promotionLevel(String project, String branch, String promotionLevel) {
        new PromotionLevelResource(
                this,
                get("structure/entity/promotionLevel/${project}/${branch}/${promotionLevel}")
        )
    }

    @Override
    ValidationStamp validationStamp(String project, String branch, String validationStamp) {
        new ValidationStampResource(
                this,
                get("structure/entity/validationStamp/${project}/${branch}/${validationStamp}")
        )
    }

    @Override
    Build build(String project, String branch, String build) {
        new BuildResource(
                this,
                get("structure/entity/build/${project}/${branch}/${build}")
        )
    }

    @Override
    def configure(Closure closure) {
        ConfigResource configResource = new ConfigResource(this)
        closure.delegate = configResource
        closure()
    }

    JsonNode get(String url) {
        jsonClient.get(url)
    }

    @Override
    JsonNode post(String url, Object data) {
        jsonClient.post(
                jsonClient.toNode(data),
                url
        )
    }

    @Override
    JsonNode put(String url, Object data) {
        jsonClient.put(
                jsonClient.toNode(data),
                url
        )
    }

    @Override
    void upload(String url, String name, Object o) {
        upload(url, name, o, 'application/x-octet-stream')
    }

    @Override
    void upload(String url, String name, Object o, String contentType) {
        Document document
        String fileName = 'file'
        if (o instanceof Document) {
            document = o as Document
        } else if (o instanceof URL) {
            URL u = o as URL
            fileName = u.file
            def connection = u.openConnection()
            document = new Document(
                    connection.getContentType(),
                    connection.inputStream.bytes
            )
        } else if (o instanceof File) {
            File file = o as File
            fileName = file.name
            document = new Document(
                    contentType,
                    file.bytes
            )
        } else if (o instanceof byte[]) {
            document = new Document(
                    contentType,
                    o as byte[]
            )
        } else if (o instanceof String) {
            def path = o as String
            if (path.startsWith('classpath:')) {
                path = path - 'classpath:'
                URL u = getClass().getResource(path)
                upload(url, name, u, contentType)
                return
            } else {
                try {
                    URL u = new URL(path)
                    upload(url, name, u, contentType)
                    return
                } catch (MalformedURLException ignored) {
                    File file = new File(path)
                    upload(url, name, file, contentType)
                    return
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported document type: ${o}")
        }
        jsonClient.upload(
                name,
                document,
                fileName,
                url
        )
    }

    @Override
    Document download(String url) {
        jsonClient.download(url)
    }
}
