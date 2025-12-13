import {generate} from "@ontrack/utils";
import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export const createValidationStamp = async (branch, name) => {
    const actualName = name ?? generate('vs_')

    const data = await graphQLCallMutation(
        branch.ontrack.connection,
        'createValidationStampById',
        gql`
            mutation CreateValidationStamp(
                $branchId: Int!,
                $name: String!,
            ) {
                createValidationStampById(input: {
                    branchId: $branchId,
                    name: $name,
                    description: "",
                }) {
                    validationStamp {
                        id
                        name
                    }
                    errors {
                        message
                    }
                }
            }
        `,
        {
            branchId: Number(branch.id),
            name: actualName,
        }
    )

    return validationStampInstance(branch, data.createValidationStampById.validationStamp)
}


const validationStampInstance = (branch, data) => {
    const validationStamp = {
        ontrack: branch.ontrack,
        ...data,
        branch,
    }

    // TODO Validation stamp methods

    return validationStamp
}