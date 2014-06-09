package net.nemerosa.ontrack.extension.jenkins.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class DefaultJenkinsClient implements JenkinsClient {

    private final ObjectMapper mapper = ObjectMapperFactory.create();
    private final JenkinsConnection jenkinsConnection;
    private final JsonClient jsonClient;
    // Local cache for user data
    private final LoadingCache<String, JenkinsUser> userCache =
            CacheBuilder.newBuilder()
                    .maximumSize(50)
                    .build(new CacheLoader<String, JenkinsUser>() {
                        @Override
                        public JenkinsUser load(String url) throws Exception {
                            return loadUser(url);
                        }
                    });

    public DefaultJenkinsClient(JenkinsConnection jenkinsConnection, JsonClient jsonClient) {
        this.jenkinsConnection = jenkinsConnection;
        this.jsonClient = jsonClient;
    }

    @Override
    public boolean hasSameConnection(JenkinsConnection connection) {
        return Objects.equals(this.jenkinsConnection, connection);
    }

    @Override
    public JenkinsJob getJob(String job, boolean depth) {
        String jobUrl = getJenkinsJobUrl(job);
        // Gets the job as JSON
        JsonNode tree = getJsonNode(jobUrl, depth);

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
                jobUrl,
                result,
                state,
                culprits,
                lastBuild
        );
    }

    private String getJenkinsJobUrl(String job) {
        return jenkinsConnection.getUrl() + "/job/" + job;
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
        try {
            return userCache.get(userUrl);
        } catch (ExecutionException e) {
            return null;
        }
    }

    protected JenkinsUser loadUser(String culpritUrl) {
        // Node
        JsonNode tree = getJsonNode(culpritUrl, false);
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

    private JsonNode getJsonNode(String url, boolean depth) {
        // Gets a client
        DefaultHttpClient client = createClient();
        // Gets the JSON API URL for the job
        String apiUrl = getAPIURL(url, depth);
        // Creates the request
        HttpGet get = new HttpGet(apiUrl);
        // Call
        HttpResponse response;
        try {
            response = client.execute(get);
        } catch (UnknownHostException ex) {
            throw new JenkinsClientNotFoundException(url);
        } catch (IOException e) {
            throw new JenkinsClientCallException(get, e);
        }
        // Checks the status
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_NOT_FOUND) {
            throw new JenkinsClientNotFoundException(url);
        } else if (statusCode != HttpStatus.SC_OK) {
            throw new JenkinsClientNotOKException(get, statusCode);
        }
        // Response entity
        HttpEntity responseEntity = response.getEntity();
        // Gets the content as UTF-8 string
        String content;
        try {
            content = responseEntity != null ? EntityUtils.toString(responseEntity, "UTF-8") : null;
        } catch (IOException e) {
            throw new JenkinsClientCannotGetContentException(get, e);
        }
        // Checks for null
        if (content == null) {
            throw new JenkinsClientNullContentException(get);
        }
        // Parses as a tree
        JsonNode tree;
        try {
            tree = mapper.readTree(content);
        } catch (IOException e) {
            throw new JenkinsClientCannotParseContentException(get, e);
        }
        return tree;
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
