import {AbstractSettings} from "@ontrack/AbstractSettings";

export class GitHubIngestionSettings extends AbstractSettings {
    constructor(ontrack) {
        super(ontrack)
    }

    async saveSettings({
                           token,
                           retentionDays,
                           orgProjectPrefix,
                           indexationInterval,
                           repositoryIncludes,
                           repositoryExcludes,
                           issueServiceIdentifier,
                           enabled,
                       }
    ) {
        return this.doSaveSettings({
            id: 'github-ingestion',
            values: {
                token,
                retentionDays,
                orgProjectPrefix,
                indexationInterval,
                repositoryIncludes,
                repositoryExcludes,
                issueServiceIdentifier,
                enabled,
            }
        })
    }
}