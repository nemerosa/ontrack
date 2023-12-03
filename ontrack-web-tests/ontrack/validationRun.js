import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export const createValidationRun = async (build, validationStamp, params) => {

    const validationStampName = validationStamp.name
    const validationRunStatus = params?.status ?? "PASSED"

    const data = await graphQLCallMutation(
        build.ontrack.connection,
        'createValidationRunById',
        gql`
            mutation ValidateBuild(
                $buildId: Int!,
                $validationStamp: String!,
                $validationRunStatus: String,
            ) {
                createValidationRunById(input: {
                    buildId: $buildId,
                    validationStamp: $validationStamp,
                    validationRunStatus: $validationRunStatus,
                }) {
                    validationRun {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        `,
        {
            buildId: build.id,
            validationStamp: validationStampName,
            validationRunStatus: validationRunStatus,
        }
    )

    return validationRunInstance(build, validationStamp, data.createValidationRunById.validationRun)

}

const validationRunInstance = (build, validationStamp, data) => {
    const run = {
        ontrack: build.ontrack,
        ...data,
        build,
        validationStamp,
    }

    // TODO Validation run methods

    return run
}
