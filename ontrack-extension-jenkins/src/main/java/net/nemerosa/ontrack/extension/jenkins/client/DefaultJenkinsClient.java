package net.nemerosa.ontrack.extension.jenkins.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.JsonClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultJenkinsClient implements JenkinsClient {

    private final JsonClient jsonClient;
    // TODO Local cache for user data

    public DefaultJenkinsClient(JsonClient jsonClient) {
        this.jsonClient = jsonClient;
    }

    @Override
    public JenkinsJob getJob(String job, boolean depth) {
        String jobPath = getJobPath(job, depth);
        // Gets the job as JSON
        JsonNode tree = getJsonNode(jobPath);

        // Name
        String name = tree.get("name").asText();

        // Gets the 'color' field
        String color = tree.get("color").asText();
        // Gets the state & result
        JenkinsJobState state = getJobState(color);
        JenkinsJobResult result = getJobResult(color);

        // Culprits
        List<JenkinsCulprit> culprits = new ArrayList<>();
        // Gets the list of builds
        JsonNode builds = tree.get("builds");
        if (builds.isArray() && builds.size() > 0) {
            JsonNode build = builds.get(0);
            if (isBuilding(build) || isFailedOrUnstable(build)) {
                // Gets the list of culprits
                if (build.has("culprits")) {
                    JsonNode jCulprits = build.get("culprits");
                    if (jCulprits.isArray()) {
                        String claim = "";
                        if (builds.size() > 1) {
                            // Gets the previous build
                            JsonNode previousBuild = builds.get(1);
                            if (previousBuild.has("actions")) {
                                for (JsonNode jAction : previousBuild.get("actions")) {
                                    if (jAction.has("claimed") && jAction.get("claimed").booleanValue()) {
                                        claim = jAction.get("claimedBy").textValue();
                                    }
                                }
                            }
                        }
                        // For each culprit
                        for (JsonNode jCulprit : jCulprits) {
                            // TODO We cannot use the absolute URL here
                            String culpritUrl = jCulprit.get("absoluteUrl").textValue();
                            JenkinsUser user = getUser(culpritUrl);
                            if (user != null) {
                                JenkinsCulprit culprit = new JenkinsCulprit(user);
                                // Claim?
                                if (StringUtils.equals(claim, culprit.getId())) {
                                    culprit = culprit.claim();
                                }
                                // OK
                                culprits.add(culprit);
                            }
                        }
                    }
                }
            }
        }

        // Last build
        JenkinsBuildLink lastBuild = toBuildLink(tree, "lastBuild");

        // OK
        return new JenkinsJob(
                name,
                jobPath,
                result,
                state,
                culprits,
                lastBuild
        );
    }

    private String getJobPath(String job, boolean depth) {
        StringBuilder path = new StringBuilder();
        path.append("/job/").append(job);
        path.append("/api/json");
        if (depth) {
            path.append("?depth=1");
        }
        return path.toString();
    }

    private JenkinsBuildLink toBuildLink(JsonNode tree, String fieldName) {
        if (tree.has(fieldName)) {
            JsonNode node = tree.get(fieldName);
            JsonNode nNumber = node.get("number");
            JsonNode nUrl = node.get("url");
            if (nNumber != null && nUrl != null) {
                return new JenkinsBuildLink(
                        nNumber.asInt(),
                        nUrl.asText()
                );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean isFailedOrUnstable(JsonNode build) {
        return build.has("result") && !"SUCCESS".equals(build.get("result").textValue());
    }

    private boolean isBuilding(JsonNode build) {
        return build.has("building") && build.get("building").booleanValue();
    }

    protected JenkinsUser getUser(String userUrl) {
//        try {
        return loadUser(userUrl);
        // TODO return userCache.get(userUrl);
//        } catch (ExecutionException e) {
//            return null;
//        }
    }

    protected JenkinsUser loadUser(String culpritUrl) {
        // Node
        // TODO Use a relative path
        JsonNode tree = getJsonNode(culpritUrl);
        // Basic data
        String id = tree.get("id").textValue();
        String fullName = tree.get("fullName").textValue();
        // Fetch the image URL
        String imageUrl = getUserImageUrl(id);
        // OK
        return new JenkinsUser(
                id,
                fullName,
                imageUrl
        );
    }

    private String getUserImageUrl(String id) {
        // TODO Image URL for the culprits
        String url = null;
        // String url = jenkinsConfiguration.getImageUrl();
        if (StringUtils.isNotBlank(url)) {
            url = StringUtils.replace(url, "*", id);
            // Gets a client
            DefaultHttpClient client = createClient();
            // Creates the request
            HttpHead head = new HttpHead(url);
            // Call
            HttpResponse response;
            try {
                response = client.execute(head);
            } catch (IOException e) {
                return getDefaultUserImageUrl();
            }
            // Checks the status
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return getDefaultUserImageUrl();
            } else {
                return url;
            }
        } else {
            return getDefaultUserImageUrl();
        }
    }

    private String getDefaultUserImageUrl() {
        return null;
    }

    protected JenkinsJobState getJobState(String color) {
        if ("disabled".equals(color)) {
            return JenkinsJobState.DISABLED;
        } else if (StringUtils.endsWith(color, "_anime")) {
            return JenkinsJobState.RUNNING;
        } else {
            return JenkinsJobState.IDLE;
        }
    }

    protected JenkinsJobResult getJobResult(String color) {
        if ("disabled".equals(color)) {
            return JenkinsJobResult.DISABLED;
        } else if (StringUtils.startsWith(color, "red")) {
            return JenkinsJobResult.FAILED;
        } else if (StringUtils.startsWith(color, "yellow")) {
            return JenkinsJobResult.UNSTABLE;
        } else if (StringUtils.startsWith(color, "blue")) {
            return JenkinsJobResult.SUCCESS;
        } else {
            return JenkinsJobResult.UNKNOWN;
        }
    }

    private JsonNode getJsonNode(String path) {
        // Gets the JSON at this URL
        return jsonClient.get(path);
    }

    private String getAPIURL(String url, boolean depth) {
        StringBuilder b = new StringBuilder(url);
        if (!url.endsWith("/")) {
            b.append("/");
        }
        b.append("api/json");
        if (depth) {
            b.append("?depth=1");
        }
        return b.toString();
    }

    private DefaultHttpClient createClient() {
        return new DefaultHttpClient(new PoolingClientConnectionManager());
    }

}
