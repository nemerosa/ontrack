import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {gql} from "graphql-request";
import BuildNameFormItem from "@components/builds/BuildNameFormItem";
import BuildDescriptionFormItem from "@components/builds/BuildDescriptionFormItem";

export const useNewBuildDialog = ({onSuccess}) => {
    return useFormDialog({
        onSuccess,
        prepareValues: (values, {branch}) => {
            return {
                ...values,
                branchId: Number(branch.id),
            }
        },
        query: gql`
            mutation CreateBuild(
                $branchId: Int!,
                $name: String!,
                $description: String,
            ) {
                createBuild(input: {
                    branchId: $branchId,
                    name: $name,
                    description: $description,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createBuild',
    })
}

export default function NewBuildDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <BuildNameFormItem/>
                <BuildDescriptionFormItem/>
            </FormDialog>
        </>
    )
}