import JenkinsNotificationChannelConfig from "@components/framework/notification-channel/jenkins/Config";

export default function MockJenkinsNotificationChannelConfig({config, job, parameters, callMode, timeout}) {
    return <JenkinsNotificationChannelConfig
        config={config}
        job={job}
        parameters={parameters}
        callMode={callMode}
        timeout={timeout}
    />
}