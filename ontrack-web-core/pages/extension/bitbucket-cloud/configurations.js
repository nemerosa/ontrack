import {Form, Input} from "antd";
import ConfigurationPage from "@components/configurations/ConfigurationPage";

export default function BitbucketCloudConfigurationsPage() {

    const columns = [
        {
            title: "Name",
            key: "name",
            dataIndex: "name"
        },
        {
            title: "Workspace",
            key: "workspace",
            dataIndex: "workspace",
        },
        {
            title: "User",
            key: "user",
            dataIndex: "user",
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
            key="workspace"
            name="workspace"
            label="Bitbucket Cloud workspace"
            rules={[{required: true, message: 'Workspace is required.',},]}
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="user"
            name="user"
            label="User"
            extra="User used by Yontrack to connect to Bitbucket Cloud."
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="password"
            name="password"
            label="Token"
            extra="Token used by Yontrack to connect to Bitbucket Cloud."
        >
            <Input.Password/>
        </Form.Item>,
    ]

    return (
        <>
            <ConfigurationPage
                pageTitle="Bitbucket Cloud configurations"
                configurationType="bitbucket-cloud"
                columns={columns}
                dialogItems={dialogItems}
            >
            </ConfigurationPage>
        </>
    )
}