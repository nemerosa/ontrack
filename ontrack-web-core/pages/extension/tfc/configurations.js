import {Form, Input} from "antd";
import Link from "next/link";
import ConfigurationPage from "@components/configurations/ConfigurationPage";

export default function TFCConfigurationsPage() {

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
            label="TFC URL"
            rules={[{required: true, message: 'URL is required.',},]}
        >
            <Input/>
        </Form.Item>,
        <Form.Item
            key="token"
            name="token"
            label="Token"
            extra="Token used to connect to TFC."
        >
            <Input.Password/>
        </Form.Item>,
    ]

    return (
        <>
            <ConfigurationPage
                pageTitle="TFC configurations"
                configurationType="tfc"
                columns={columns}
                dialogItems={dialogItems}
            >
            </ConfigurationPage>
        </>
    )
}