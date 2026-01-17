import {gql} from "graphql-request";

const maxDownstreamDepth = 5
const maxUpstreamDepth = 5

const gqlBuildInfo = gql`
    fragment BuildInfo on Build {
        id
        name
        branch {
            id
            name
            project {
                id
                name
            }
        }
        releaseProperty {
            value
        }
        creation {
            time
        }
        promotionRuns(lastPerLevel: true) {
            id
            creation {
                time
            }
            promotionLevel {
                id
                description
                name
                image
            }
        }
    }
`

const gqlBranchInfo = gql`
    fragment BranchInfo on Branch {
        id
        name
        displayName
        project {
            id
            name
        }
    }
`

const gqlBranchNodeInfo = gql`
    fragment BranchNodeInfo on Branch {
        ...BranchInfo
        latestBuilds: builds(count: 1) {
            ...BuildInfo
        }
    }
`

const gqlDownstreamDependencies = (depth) => {
    if (depth <= 0) {
        return ''
    } else {
        return `
                downstreamLinks(builds: 5) {
                    qualifier
                    sourceBuild {
                        ...BuildInfo
                    }
                    targetBuild {
                        ...BuildInfo
                    }
                    autoVersioning {
                        lastEligibleBuild {
                            ...BuildInfo
                        }
                        status {
                            order {
                                uuid
                                targetVersion
                            }
                            running
                            mostRecentState {
                              state
                              running
                              processing
                              creation {
                                  time
                              }
                              data
                            }
                        }
                    }
                    branch {
                        ...BranchNodeInfo
                        ${gqlDownstreamDependencies(depth - 1)}
                    }
                }
            `
    }
}

const gqlUpstreamDependencies = (depth) => {
    if (depth <= 0) {
        return ''
    } else {
        return `
                upstreamLinks(builds: 5) {
                    qualifier
                    sourceBuild {
                        ...BuildInfo
                    }
                    targetBuild {
                        ...BuildInfo
                    }
                    branch {
                        ...BranchNodeInfo
                        ${gqlUpstreamDependencies(depth - 1)}
                    }
                }
            `
    }
}

export const branchQuery = `
        query RootBranch($branchId: Int!) {
            branch(id: $branchId) {
                ...BranchNodeInfo
                ${gqlDownstreamDependencies(maxDownstreamDepth)}
                ${gqlUpstreamDependencies(maxUpstreamDepth)}
            }
        }
        ${gqlBuildInfo}
        ${gqlBranchInfo}
        ${gqlBranchNodeInfo}
    `
