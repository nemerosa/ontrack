import StandardPage from "@components/layouts/StandardPage";
import {useContext, useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Form, Input, message, Space, Table} from "antd";
import Link from "next/link";
import JenkinsConfigurationDialog, {
    useJenkinsConfigurationDialog
} from "@components/extension/jenkins/JenkinsConfigurationDialog";
import {UserContext} from "@components/providers/UserProvider";
import {FaPencilAlt, FaPlus, FaQuestionCircle} from "react-icons/fa";
import {Command} from "@components/common/Commands";
import InlineCommand from "@components/common/InlineCommand";
import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import ConfigurationPage from "@components/configurations/ConfigurationPage";

const {Column} = Table

export default function JenkinsConfigurationsPage() {

    const user = useContext(UserContext)
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [configurations, setConfigurations] = useState([])
    const [refresh, setRefresh] = useState(0)

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query JenkinsConfigurations {
                        jenkinsConfigurations {
                            name
                            url
                            user
                        }
                    }
                `
            ).then(data => {
                setConfigurations(data.jenkinsConfigurations)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, refresh]);

    const reload = () => {
        setRefresh(refresh + 1)
    }

    const dialog = useJenkinsConfigurationDialog({onSuccess: reload})
    const [messageApi, contextHolder] = message.useMessage()

    const onCreateConfig = () => {
        dialog.start({creation: true})
    }

    const onTestConfig = (config) => {
        return () => {
            client.request(
                gql`
                    mutation TestJenkinsConfiguration(
                        $name: String!,
                        $url: String!,
                        $user: String,
                        $password: String,
                    ) {
                        testJenkinsConfiguration(input: {
                            name: $name,
                            url: $url,
                            user: $user,
                            password: $password,
                        }) {
                            connectionResult {
                                type
                                message
                            }
                            errors {
                                message
                            }
                        }
                    }
                `,
                config
            ).then(data => {
                let connectionResult = undefined
                const node = data.testJenkinsConfiguration
                if (node.errors) {
                    const {message} = node.errors[0]
                    connectionResult = {
                        type: 'ERROR',
                        message,
                    }
                } else {
                    connectionResult = node.connectionResult
                }
                if (connectionResult) {
                    if (connectionResult.type === 'OK') {
                        messageApi.success("Connection OK")
                    } else {
                        messageApi.error(connectionResult.message)
                    }
                }
            })
        }
    }

    const onUpdateConfig = (config) => {
        return () => {
            dialog.start({config})
        }
    }

    const deleteConfig = (config) => {
        client.request(
            gql`
                mutation DeleteJenkinsConfiguration($name: String!) {
                    deleteJenkinsConfiguration(input: {name: $name}) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                name: config.name,
            }
        ).then(reload)
    }

    // ==== Generic

    const columns = [
        {
            title: "Name",
            key: "name",
            dataIndex: "name"
        },
        {
            title: "URL",
            key: "url",
            dataIndex: "url",
            render: (value) => <Link href={value}>{value}</Link>,
        },
        {
            title: "User",
            key: "user",
            dataIndex: "user"
        },
    ]

    const dialogItems = [
        <Form.Item
            name="name"
            label="Configuration name"
            rules={[{required: true, message: 'Name is required.',},]}
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            name="url"
            label="Jenkins URL"
            rules={[{required: true, message: 'URL is required.',},]}
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            name="user"
            label="Jenkins username"
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            name="password"
            label="Jenkins password"
        >
            <Input.Password/>
        </Form.Item>,
    ]

    return (
        <>
            <ConfigurationPage
                pageTitle="Jenkins configurations"
                configurationType="jenkins"
                columns={columns}
                dialogItems={dialogItems}
            >
            </ConfigurationPage>
            {contextHolder}
            {/*<StandardPage*/}
            {/*    pageTitle="Jenkins configurations"*/}
            {/*    additionalCommands={*/}
            {/*        user?.authorizations?.global?.settings ? [*/}
            {/*            <Command icon={<FaPlus/>} action={onCreateConfig} text="Create Jenkins config"/>*/}
            {/*        ] : []*/}
            {/*    }*/}
            {/*>*/}
            {/*    <Table*/}
            {/*        loading={loading}*/}
            {/*        dataSource={configurations}*/}
            {/*        pagination={false}*/}
            {/*    >*/}
            {/*        <Column*/}
            {/*            title="Name"*/}
            {/*            key="name"*/}
            {/*            dataIndex="name"*/}
            {/*        />*/}
            {/*        <Column*/}
            {/*            title="URL"*/}
            {/*            key="url"*/}
            {/*            dataIndex="url"*/}
            {/*            render={(value) => <Link href={value}>{value}</Link>}*/}
            {/*        />*/}
            {/*        <Column*/}
            {/*            title="User"*/}
            {/*            key="user"*/}
            {/*            dataIndex="user"*/}
            {/*        />*/}
            {/*        <Column*/}
            {/*            key="actions"*/}
            {/*            render={(_, config) =>*/}
            {/*                <Space>*/}
            {/*                    {*/}
            {/*                        user?.authorizations?.global?.settings &&*/}
            {/*                        <Space>*/}
            {/*                            <InlineCommand*/}
            {/*                                title="Tests this configuration"*/}
            {/*                                icon={<FaQuestionCircle/>}*/}
            {/*                                onClick={onTestConfig(config)}*/}
            {/*                            />*/}
            {/*                            <InlineCommand*/}
            {/*                                title="Updates this configuration"*/}
            {/*                                icon={<FaPencilAlt/>}*/}
            {/*                                onClick={onUpdateConfig(config)}*/}
            {/*                            />*/}
            {/*                            <InlineConfirmCommand*/}
            {/*                                title="Deletes the configuration"*/}
            {/*                                confirm="Do you really want to delete this configuration?"*/}
            {/*                                onConfirm={() => deleteConfig(config)}*/}
            {/*                            />*/}
            {/*                        </Space>*/}
            {/*                    }*/}
            {/*                </Space>*/}
            {/*            }*/}
            {/*        />*/}
            {/*    </Table>*/}
            {/*</StandardPage>*/}
            {/*<JenkinsConfigurationDialog jenkinsConfigurationDialog={dialog}/>*/}
        </>
    )
}