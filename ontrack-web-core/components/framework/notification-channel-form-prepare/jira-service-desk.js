export default function JiraServiceDeskNotificationChannelFormPrepare(channelConfig) {
    return {
        ...channelConfig,
        customFields: channelConfig.fields ? channelConfig.fields.map(({name, value}) => ({
            name,
            value: JSON.parse(value)
        })) : [],
    }
}
