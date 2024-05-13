import JenkinsNotificationChannelOutput from "@components/framework/notification-channel/jenkins/Output";

export default function MockJenkinsNotificationChannelOutput({jobUrl, parameters = []}) {
    return <JenkinsNotificationChannelOutput
        jobUrl={jobUrl}
        parameters={parameters}
    />
}