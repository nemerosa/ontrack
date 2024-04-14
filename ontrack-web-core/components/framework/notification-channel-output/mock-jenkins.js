import JenkinsNotificationChannelOutput from "@components/framework/notification-channel-output/jenkins";

export default function MockJenkinsNotificationChannelOutput({jobUrl, parameters = []}) {
    return <JenkinsNotificationChannelOutput
        jobUrl={jobUrl}
        parameters={parameters}
    />
}