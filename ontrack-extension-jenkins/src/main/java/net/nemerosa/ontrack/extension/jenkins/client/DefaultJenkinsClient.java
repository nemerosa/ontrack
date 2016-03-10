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
    public JenkinsJob getJob(String job) {
        String path = job.replaceAll("/job/", "/").replaceAll("/", "/job/");
        String jobPath = jsonClient.getUrl("job/%s", path);
        String jobName = StringUtils.substringAfterLast(jobPath, "/");
        return new JenkinsJob(
                jobName,
                jobPath
        );
    }

    @Override
    public JenkinsInfo getInfo() {
        JsonNode tree = jsonClient.get("/api/json");
        return new JenkinsInfo(
                tree.path("slaveAgentPort").asInt()
        );
    }

}
