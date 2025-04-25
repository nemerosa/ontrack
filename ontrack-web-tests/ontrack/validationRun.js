import {graphQLCall, graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

const validationRunFragment = gql`
    fragment ValidationRunData on ValidationRun {
        id
        build {
            id
            name
        }
        validationStamp {
            id
            name
        }
        lastStatus {
            statusID {
                id
            }
        }
    }
`

export const createValidationRun = async (build, validationStamp, params) => {

    const validationStampName = validationStamp.name
    const validationRunStatus = params?.status ?? "PASSED"
    const description = params?.description

    const data = await graphQLCallMutation(
        build.ontrack.connection,
        'createValidationRunById',
        gql`
            mutation ValidateBuild(
                $buildId: Int!,
                $validationStamp: String!,
                $validationRunStatus: String,
                $description: String,
            ) {
                createValidationRunById(input: {
                    buildId: $buildId,
                    validationStamp: $validationStamp,
                    validationRunStatus: $validationRunStatus,
                    description: $description,
                }) {
                    validationRun {
                        ...ValidationRunData
                    }
                    errors {
                        message
                    }
                }
            }
            
            ${validationRunFragment}
        `,
        {
            buildId: Number(build.id),
            validationStamp: validationStampName,
            validationRunStatus: validationRunStatus,
            description,
        }
    )

    return validationRunInstance(build.ontrack, build, validationStamp, data.createValidationRunById.validationRun)

}

export const getValidationRunById = async (ontrack, runId) => {
    const data = await graphQLCall(
        ontrack.connection,
        gql`
            query GetValidationRunById(
                $runId: Int!,
            ) {
                validationRuns(id: $runId) {
                    ...ValidationRunData
                }
            }

            ${validationRunFragment}
        `,
        {
            runId,
        }
    )

    const run = data.validationRuns[0]

    return validationRunInstance(ontrack, run.build, run.validationStamp, run)
}

const validationRunInstance = (ontrack, build, validationStamp, data) => {
    const run = {
        ontrack,
        ...data,
        build,
        validationStamp,
    }

    // TODO Validation run methods

    return run
}
