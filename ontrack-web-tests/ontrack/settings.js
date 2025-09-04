import {GitHubIngestionSettings} from "@ontrack/extensions/github/GitHubIngestionSettings";

export class OntrackSettings {
    constructor(ontrack) {
        this.ontrack = ontrack
        this.gitHubIngestion = new GitHubIngestionSettings(ontrack)
    }
}
