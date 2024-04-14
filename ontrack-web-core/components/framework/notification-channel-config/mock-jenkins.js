import JenkinsNotificationChannelConfig from "@components/framework/notification-channel-config/jenkins";

export default function MockJenkinsNotificationChannelConfig({config, job, parameters, callMode, timeout}) {
    return <JenkinsNotificationChannelConfig
        config={config}
        job={job}
        parameters={parameters}
        callMode={callMode}
        timeout={timeout}
    />
}