import {Form, Input, Select, Tag} from "antd";
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
            key="user"
            name="user"
            label="Username"
            extra="Username used to connect to Jira."
            rules={[{required: true, message: 'Username is required.',},]}
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="password"
            name="password"
            label="Password"
            extra="Password or token used to connect to Jira."
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