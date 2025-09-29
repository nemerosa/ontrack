import {Form, Input} from "antd";
import Link from "next/link";
import ConfigurationPage from "@components/configurations/ConfigurationPage";

export default function BitbucketServerConfigurationsPage() {

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
            title: "Auto-merge user",
            key: "autoMergeUser",
            dataIndex: "autoMergeUser",
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
            label="Bitbucket Server URL"
            rules={[{required: true, message: 'URL is required.',},]}
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="user"
            name="user"
            label="User"
            extra="User used by Yontrack to connect to Bitbucket Server."
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="password"
            name="password"
            label="Token"
            extra="Token used by Yontrack to connect to Bitbucket Server."
        >
            <Input.Password/>
        </Form.Item>,
        <Form.Item
            key="autoMergeUser"
            name="autoMergeUser"
            label="Auto-merge user"
            extra="Slug of the user approving pull requests for the auto merge operations."
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="autoMergeToken"
            name="autoMergeToken"
            label="Auto-merge token"
            extra="Token used for approving pull requests for the auto merge operations."
        >
            <Input.Password/>
        </Form.Item>,
    ]

    return (
        <>
            <ConfigurationPage
                pageTitle="Bitbucket Server configurations"
                configurationType="stash"
                columns={columns}
                dialogItems={dialogItems}
            >
            </ConfigurationPage>
        </>
    )
}