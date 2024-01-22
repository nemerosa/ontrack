import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Button} from "antd";
import {Fragment, useState} from "react";
import ConnectionResult from "@components/configurations/ConnectionResult";
import {gql} from "graphql-request";
import {prepareConfigValues, testConfig} from "@components/configurations/ConfigurationUtils";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export const useConfigurationDialog = ({onSuccess, dialogItems, configurationType}) => {
    return useFormDialog({
        init: (form, {config}) => {
            if (config) {
                form.setFieldsValue(config)
            }
        },
        onSuccess,
        dialogItems,
        configurationType,
        prepareValues: (values) => {
            return prepareConfigValues(values, configurationType)
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

    const client = useGraphQLClient()
    const [connectionResult, setConnectionResult] = useState()

    const onTestConfig = async () => {
        setConnectionResult(undefined)
        const connectionResult = await testConfig(client, configurationDialog.form.getFieldsValue(true), configurationDialog.configurationType)
        setConnectionResult(connectionResult)
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