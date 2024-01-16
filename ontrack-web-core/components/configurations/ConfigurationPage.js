import StandardPage from "@components/layouts/StandardPage";
import {createContext, useContext, useEffect, useState} from "react";
import {Space, Table} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {UserContext} from "@components/providers/UserProvider";
import {Command} from "@components/common/Commands";
import {FaPencilAlt, FaPlus, FaQuestionCircle} from "react-icons/fa";
import ConfigurationDialog, {useConfigurationDialog} from "@components/configurations/ConfigurationDialog";
import InlineCommand from "@components/common/InlineCommand";
import InlineConfirmCommand from "@components/common/InlineConfirmCommand";

export default function ConfigurationPage({
                                              pageTitle,
                                              configurationType,
                                              columns = [],
                                              dialogItems = [],
                                          }) {

    const user = useContext(UserContext)
    const client = useGraphQLClient()

    const [refresh, setRefresh] = useState(0)

    const [loading, setLoading] = useState(true)
    const [configurations, setConfigurations] = useState([])

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query ConfigurationList($configurationType: String!) {
                        configurations(configurationType: $configurationType) {
                            name
                            data
                        }
                    }
                `,
                {
                    configurationType,
                }
            ).then(data => {
                setConfigurations(data.configurations.map(it => ({
                    ...it.data,
                    name: it.name,
                })))
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, configurationType, refresh]);

    const reload = () => {
        setRefresh(refresh + 1)
    }

    const dialog = useConfigurationDialog({
        onSuccess: reload,
        dialogItems,
        configurationType,
    })

    const onCreateConfig = () => {
        dialog.start({creation: true})
    }

    const onTestConfig = (config) => {
        return () => {

        }
    }

    const onUpdateConfig = (config) => {
        return () => {

        }
    }

    const onDeleteConfig = (config) => {
        return () => {

        }
    }

    const actionsColumn = {
        key: "actions",
        render: (_, config) =>
            <Space>
                {
                    user?.authorizations?.global?.settings &&
                    <Space>
                        <InlineCommand
                            title="Tests this configuration"
                            icon={<FaQuestionCircle/>}
                            onClick={onTestConfig(config)}
                        />
                        <InlineCommand
                            title="Updates this configuration"
                            icon={<FaPencilAlt/>}
                            onClick={onUpdateConfig(config)}
                        />
                        <InlineConfirmCommand
                            title="Deletes the configuration"
                            confirm="Do you really want to delete this configuration?"
                            onConfirm={onDeleteConfig(config)}
                        />
                    </Space>
                }
            </Space>,
    }

    return (
        <>
            <StandardPage
                pageTitle={pageTitle}
                additionalCommands={
                    user?.authorizations?.global?.settings ? [
                        <Command icon={<FaPlus/>} action={onCreateConfig} text="Create config"/>
                    ] : []
                }
            >
                <Table
                    loading={loading}
                    dataSource={configurations}
                    columns={[...columns, actionsColumn]}
                />
            </StandardPage>
            <ConfigurationDialog configurationDialog={dialog}/>
        </>
    )
}
