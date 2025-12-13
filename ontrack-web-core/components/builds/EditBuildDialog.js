import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import BuildNameFormItem from "@components/builds/BuildNameFormItem";
import BuildDescriptionFormItem from "@components/builds/BuildDescriptionFormItem";
import {gql} from "graphql-request";

export const useEditBuildDialog = ({onSuccess}) => {
    return useFormDialog({
        init: (form, {build}) => {
            form.setFieldsValue(build)
        },
        prepareValues: (values, {build}) => {
            return {
                ...values,
                buildId: Number(build.id),
            }
        },
        query: gql`
            mutation EditBuild(
                $buildId: Int!,
                $name: String!,
                $description: String,
            ) {
                updateBuild(input: {
                    id: $buildId,
                    name: $name,
                    description: $description,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'updateBuild',
        onSuccess,
    })
}

export default function EditBuildDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <BuildNameFormItem/>
                <BuildDescriptionFormItem/>
            </FormDialog>
        </>
    )
}