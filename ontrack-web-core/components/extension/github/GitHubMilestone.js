import GitHubState from "@components/extension/github/GitHubState";

export default function GitHubMilestone({milestone}) {
    return (
        <>
            {
                milestone &&
                <GitHubState
                    state={milestone.state}
                    text={milestone.title}
                />
            }
        </>
    )
}