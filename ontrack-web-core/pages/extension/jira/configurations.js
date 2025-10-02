import {Form, Input, Select, Tag, Typography} from "antd";
import Link from "next/link";
import ConfigurationPage from "@components/configurations/ConfigurationPage";

export default function JiraConfigurationsPage() {

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
            title: "API URL",
            key: "apiUrl",
            dataIndex: "apiUrl",
            render: (value) => <Typography.Text code>{value}</Typography.Text>,
        },
        {
            title: "User",
            key: "user",
            dataIndex: "user",
        },
        {
            title: "Includes",
            key: "includes",
            render: (_, {include}) => {
                return <>
                    {
                        include.map((it, index) => <Tag key={index} color="success">{it}</Tag>)
                    }
                </>
            }
        },
        {
            title: "Excludes",
            key: "excludes",
            render: (_, {exclude}) => {
                return <>
                    {
                        exclude.map((it, index) => <Tag key={index} color="error">{it}</Tag>)
                    }
                </>
            }
        },
    ]

    const dialogItems = [
        <Form.Item
            key="name"
            name="name"
            label="Configuration name"
            rules={[{required: true, message: 'Name is required.',},]}
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="url"
            name="url"
            label="Jira URL"
            rules={[{required: true, message: 'URL is required.',},]}
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="apiUrl"
            name="apiUrl"
            label="API URL"
            help="Alternative URL to access the Jira API (used for Jira Cloud)"
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="user"
            name="user"
            label="Username"
            extra="Username used to connect to Jira. If blank, the password will be used as a personal access token (PAT)."
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="password"
            name="password"
            label="Password"
            extra="Password or token used to connect to Jira. It's considered a personal access token (PAT) if the username is blank."
        >
            <Input.Password/>
        </Form.Item>,
        <Form.Item
            key="include"
            name="include"
            label="Include"
            extra="List of regular expressions for the projects to include (empty = default = all)"
        >
            <Select
                mode="tags"
                style={{
                    width: '100%',
                }}
                placeholder="Includes all"
                options={[]}
            />
        </Form.Item>,
        <Form.Item
            key="exclude"
            name="exclude"
            label="Exclude"
            extra="List of regular expressions for the projects to exclude (empty = default = none)"
        >
            <Select
                mode="tags"
                style={{
                    width: '100%',
                }}
                placeholder="Excludes none"
                options={[]}
            />
        </Form.Item>,
    ]

    return (
        <>
            <ConfigurationPage
                pageTitle="Jira configurations"
                configurationType="jira"
                columns={columns}
                dialogItems={dialogItems}
            >
            </ConfigurationPage>
        </>
    )
}