import {generate} from "@ontrack/utils";
import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {ontrack} from "@ontrack/ontrack";
import {restCallPost, restCallPostForJson} from "@ontrack/rest";

export const createMockSCMContext = () => {
    // Unique name for the repository
    const repositoryName = generate("repo-")
    // Creating the repository context
    return new MockSCMContext(repositoryName)
}

class MockSCMContext {

    constructor(repositoryName) {
        this.repositoryName = repositoryName
        this.commitIdsPerMessage = {}
    }

    async configureProjectForMockSCM(project, issueServiceIdentifier) {
        return graphQLCallMutation(
            project.ontrack.connection,
            'setProjectPropertyById',
            gql`
                mutation SetProjectMockSCM(
                    $projectId: Int!,
                    $value: JSON!,
                ) {
                    setProjectPropertyById(input: {
                        id: $projectId,
                        property: "net.nemerosa.ontrack.extension.scm.mock.MockSCMProjectPropertyType",
                        value: $value
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                projectId: Number(project.id),
                value: {
                    name: this.repositoryName,
                    issueServiceIdentifier: issueServiceIdentifier,
                }
            }
        )
    }

    async configureBranchForMockSCM(branch, scmBranch = 'main') {
        return graphQLCallMutation(
            branch.ontrack.connection,
            'setBranchPropertyById',
            gql`
                mutation SetBranchMockSCM(
                    $branchId: Int!,
                    $value: JSON!,
                ) {
                    setBranchPropertyById(input: {
                        id: $branchId,
                        property: "net.nemerosa.ontrack.extension.scm.mock.MockSCMBranchPropertyType",
                        value: $value
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                branchId: Number(branch.id),
                value: {
                    name: scmBranch
                }
            }
        )
    }

    async configureBuildForMockSCM(build, commitId) {
        return graphQLCallMutation(
            build.ontrack.connection,
            'setBuildPropertyById',
            gql`
                mutation SetBuildMockSCM(
                    $buildId: Int!,
                    $value: JSON!,
                ) {
                    setBuildPropertyById(input: {
                        id: $buildId,
                        property: "net.nemerosa.ontrack.extension.scm.mock.MockSCMBuildCommitPropertyType",
                        value: $value
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                buildId: Number(build.id),
                value: {
                    id: commitId
                }
            }
        )
    }

    async repositoryIssue({key, summary, type, issueServiceId, linkedKey}) {
        const response = await restCallPost(
            ontrack().connection,
            `/extension/${issueServiceId ?? 'scm'}/mock/issue`,
            {
                name: this.repositoryName,
                key,
                message: summary,
                type,
                linkedKey,
            }
        )
        if (!response.ok) {
            const message = `Could not create mock issue ${key}`;
            console.log(message)
            throw message
        }
    }

    async repositoryCommit({branch = 'main', message}) {
        const json = await restCallPostForJson(
            ontrack().connection,
            "/extension/scm/mock/commit",
            {
                name: this.repositoryName,
                scmBranch: branch,
                message,
            }
        )
        const id = json.commitId;
        this.commitIdsPerMessage[message] = id
        return id
    }

    async setBuildWithCommits(buildPromise, commits, branch = 'main') {
        const build = await buildPromise
        // Creates the commits and lake the last one
        let commitId = ''
        for (const message of commits) {
            commitId = await this.repositoryCommit({branch, message})
        }
        // Sets the commit on the build
        await this.configureBuildForMockSCM(build, commitId)
        // Returning the build
        return build
    }

}
