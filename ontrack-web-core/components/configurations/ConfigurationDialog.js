import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Button} from "antd";
import {Fragment, useState} from "react";
import ConnectionResult from "@components/configurations/ConnectionResult";
import {gql} from "graphql-request";

export const useConfigurationDialog = ({onSuccess, dialogItems, configurationType}) => {
    return useFormDialog({
        init: (form, {config}) => {
            if (config) {
                form.setFieldsValue(config)
            }
        },
        onSuccess,
        dialogItems,
        prepareValues: (values) => {
            // The mutation expects three fields:
            // - type: configuration type
            // - name: the name of the configuration
            // - data: the values for this configuration
            // Right now, all values are flattened into the values
            values.data = {...values}
            delete values.data.name
            values.type = configurationType
        },
        query: ({creation}) => creation ?
            gql`
                mutation CreateConfiguration(
                    $type: String!,
                    $name: String!,
                    $data: JSON!,
                ) {
                    createConfiguration(input: {
                        type: $type,
                        name: $name,
                        data: $data,
                    }) {
                        errors {
                            message
                        }
                    }
                }            
            ` :
            gql`
                mutation UpdateConfiguration(
                    $type: String!,
                    $name: String!,
                    $data: JSON!,
                ) {
                    updateConfiguration(input: {
                        type: $type,
                        name: $name,
                        data: $data,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
        userNode: ({creation}) => creation ? 'createConfiguration' : 'updateConfiguration',
    })
}

export default function ConfigurationDialog({configurationDialog}) {

    const [connectionResult, setConnectionResult] = useState()

    const onTestConfig = () => {
        // TODO
    }

    return (
        <>
            <FormDialog
                extraButtons={
                    <Button type="default" onClick={onTestConfig}>
                        Test
                    </Button>
                }
                dialog={configurationDialog}
            >
                {
                    configurationDialog.dialogItems.map(item =>
                        <Fragment key={item.name}>
                            {item}
                        </Fragment>
                    )
                }
                <ConnectionResult connectionResult={connectionResult}/>
            </FormDialog>
        </>
    )
}