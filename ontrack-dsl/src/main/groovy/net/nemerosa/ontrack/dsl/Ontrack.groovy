package net.nemerosa.ontrack.dsl

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.http.OTHttpClient
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity

/**
 * Entry point for the DSL.
 */
@DSL(value = "An Ontrack instance is usually bound to the `ontrack` identifier and is the root for all DSL calls.")
class Ontrack {

    /**
     * HTTP client
     */
    private final OTHttpClient httpClient
    private final JsonSlurper jsonSlurper = new JsonSlurper()

    /**
     * Construction of the Ontrack client, based on a raw HTTP client
     */
    Ontrack(OTHttpClient httpClient) {
        this.httpClient = httpClient
    }

    /**
     * Gets the list of projects
     */
    @DSLMethod("Gets the list of projects")
    List<Project> getProjects() {
        return get('structure/projects').resources
    }

    /**
     * Looks for a project by its name
     * @param name Name of the project
     * @return Project or null is not found
     */
    @DSLMethod("Finds a project using its name. Returns null if not found.")
    Project findProject(String name) {
        def projectNode = get("structure/projects").resources.find {
            it.name == name
        }
        if (projectNode) {
            return new Project(this, get(projectNode._self))
        } else {
            return null
        }
    }

    @DSLMethod(value = "Finds or creates a project.", id = "project", count = 2)
    Project project(String name, String description = '') {
        def project = findProject(name)
        if (project) {
            return project
        }
        // If it does not exist, creates it
        else {
            new Project(
                    this,
                    post(
                            'structure/projects/create',
                            [
                                    name       : name,
                                    description: description
                            ]
                    )
            )
        }
    }

    @DSLMethod(value = "Finds or creates a project, and configures it.", id = "project-closure", count = 3)
    Project project(String name, String description = '', Closure closure) {
        def project = project(name, description)
        project.call(closure)
        project
    }

    @DSLMethod(value = "Looks for a branch in a project. Fails if not found.")
    Branch branch(String project, String branch) {
        new Branch(
                this,
                get("structure/entity/branch/${project}/${branch}")
        )
    }

    @DSLMethod(value = "Looks for a promotion level by name. Fails if not found.")
    PromotionLevel promotionLevel(String project, String branch, String promotionLevel) {
        new PromotionLevel(
                this,
                get("structure/entity/promotionLevel/${project}/${branch}/${promotionLevel}")
        )
    }

    @DSLMethod(value = "Looks for a validation stamp by name. Fails if not found.")
    ValidationStamp validationStamp(String project, String branch, String validationStamp) {
        new ValidationStamp(
                this,
                get("structure/entity/validationStamp/${project}/${branch}/${validationStamp}")
        )
    }

    @DSLMethod(value = "Looks for a build by name. Fails if not found.")
    Build build(String project, String branch, String build) {
        new Build(
                this,
                get("structure/entity/build/${project}/${branch}/${build}")
        )
    }

    @DSLMethod(value = "Launches a global search based on a token.")
    List<SearchResult> search(String token) {
        post('search', [token: token]).collect {
            new SearchResult(this, it)
        }
    }

    @DSLMethod(value = "Configures the general settings of Ontrack. See <<dsl-config>>.")
    def configure(Closure closure) {
        Config configResource = new Config(this)
        closure.delegate = configResource
        closure()
    }

    @DSLMethod(value = "Access to the general configuration of Ontrack")
    Config getConfig() {
        new Config(this)
    }

    @DSLMethod(value = "Access to the administration of Ontrack")
    Admin getAdmin() {
        new Admin(this)
    }

    @DSLMethod(value = "Runs an arbitrary GET request for a relative path and returns JSON")
    def get(String url) {
        httpClient.get(url) { jsonSlurper.parseText(it) }
    }

    @DSLMethod(value = "Runs an arbitrary GET request for a relative path and returns text")
    def text(String url) {
        httpClient.get(url) { it }
    }

    @DSLMethod(value = "Runs an arbitrary DELETE request for a relative path and returns JSON")
    def delete(String url) {
        httpClient.delete(url) { jsonSlurper.parseText(it) }
    }

    @DSLMethod(value = "Runs an arbitrary POST request for a relative path and some data, and returns JSON")
    def post(String url, Object data) {
        httpClient.post(
                url,
                new StringEntity(
                        new JsonBuilder(data).toPrettyString(),
                        ContentType.create("application/json", "UTF-8")
                )
        ) { jsonSlurper.parseText(it) }
    }

    @DSLMethod(value = "Runs an arbitrary PUT request for a relative path and some data, and returns JSON")
    def put(String url, Object data) {
        httpClient.put(
                url,
                new StringEntity(
                        new JsonBuilder(data).toPrettyString(),
                        ContentType.create("application/json", "UTF-8")
                )
        ) { jsonSlurper.parseText(it) }
    }

    @DSLMethod(value = "Uploads some arbitrary binary data on a relative path and returns some JSON. See <<dsl-ontrack-upload-type>>.")
    def upload(String url, String name, Object o) {
        upload(url, name, o, 'application/x-octet-stream')
    }

    @DSLMethod(value = "Uploads some typed data on a relative path and returns some JSON", id = "upload-type")
    def upload(String url, String name, Object o, String contentType) {
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
        httpClient.upload(
                url,
                name,
                fileName,
                document,
        ) { it ? jsonSlurper.parseText(it) : [:] }
    }

    @DSLMethod(value = "Downloads an arbitrary document using a relative path.")
    Document download(String url) {
        httpClient.download(url)
    }
}
