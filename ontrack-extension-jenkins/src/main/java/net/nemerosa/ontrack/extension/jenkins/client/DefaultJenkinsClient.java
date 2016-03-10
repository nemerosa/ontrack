package net.nemerosa.ontrack.extension.jenkins.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.JsonClient;
import org.apache.commons.lang3.StringUtils;

public class DefaultJenkinsClient implements JenkinsClient {

    private final JsonClient jsonClient;

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

        // OK
        return new JenkinsJob(
                name,
                jobPath,
                result,
                state
        );
    }

    @Override
    public JenkinsInfo getInfo() {
        JsonNode tree = getJsonNode("/api/json");
        return new JenkinsInfo(
                tree.path("slaveAgentPort").asInt()
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

}
