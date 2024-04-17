export default function JiraCreationNotificationChannelFormPrepare(channelConfig) {
    return {
        ...channelConfig,
        customFields: channelConfig.customFields.map(({name, value}) => ({
            name,
            value: JSON.parse(value)
        })),
    }
}
