import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";

export default function GitIssueInfoView({projectId, issueKey}) {
    const {loading, data} = useQuery(
        gql`
            query GitIssueInfo(
                $projectId: Int!,
                $issueKey: String!,
            ) {
                project(id: $projectId) {
                    id
                    name
                    scmIssueInfo(issueKey: $issueKey) {
                        # TODO Issue info
                        scmCommitInfo {
                            scmDecoratedCommit {
                                commit {
                                    id
                                    shortId
                                    author
                                    timestamp
                                    message
                                    link
                                }
                                annotatedMessage
                            }
                            branchInfos {
                                type
                                branchInfoList {
                                    branch {
                                        id
                                        name
                                        displayName
                                        disabled
                                        project {
                                            id
                                            name
                                        }
                                    }
                                    firstBuild {
                                        id
                                        name
                                        displayName
                                        creation {
                                            time
                                        }
                                    }
                                    promotions {
                                        id
                                        promotionLevel {
                                            id
                                            name
                                            image
                                        }
                                        build {
                                            id
                                            name
                                            displayName
                                        }
                                        creation {
                                            time
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        `,
        {
            initialData: {
                project: {
                    id: projectId,
                    name: ""
                }
            },
            variables: {
                projectId: Number(projectId),
                commit,
            },
            deps: [projectId, commit],
        }
    )
}