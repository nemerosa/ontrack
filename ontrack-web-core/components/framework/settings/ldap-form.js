import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input, Switch} from "antd";
import {useState} from "react";

export default function ({id, ...values}) {

    const [enabled, setEnabled] = useState(values.enabled)

    const onValuesChange = (values) => {
        if (values.enabled !== undefined) {
            setEnabled(values.enabled)
        }
    }

    return (
        <>
            <SettingsForm id={id} values={values} onValuesChange={onValuesChange}>
                <Form.Item
                    name="enabled"
                    label="Enabled"
                    extra="Is LDAP authentication enabled?"
                >
                    <Switch/>
                </Form.Item>
                {
                    enabled &&
                    <>
                        <Form.Item
                            name="url"
                            label="URL"
                            extra="URL to the LDAP server. For example: https://ldap.nemerosa.com:636"
                        >
                            <Input style={{width: '36em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="user"
                            label="User"
                            extra="Name of the user used to connect to the LDAP server."
                        >
                            <Input style={{width: '24em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="password"
                            label="Password"
                            extra="Password of the user used to connect to the LDAP server."
                        >
                            <Input.Password style={{width: '24em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="searchBase"
                            label="Search base"
                            extra={
                                <>
                                    Query to get the user. For example: <code>dc=nemerosa,dc=com</code>
                                </>
                            }
                        >
                            <Input style={{width: '24em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="searchFilter"
                            label="Search filter"
                            extra={
                                <>
                                    Filter on the user query. <code>{'{'}0{'}'}</code> will be replaced by the user name. For
                                    example: <code>(sAMAccountName={'{'}0{'}'})</code>
                                </>
                            }
                        >
                            <Input style={{width: '24em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="fullNameAttribute"
                            label="Full name attribute"
                            extra={
                                <>
                                    Name of the attribute that contains the full name of the user. Defaults to <code>cn</code>.
                                </>
                            }
                        >
                            <Input style={{width: '12em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="emailAttribute"
                            label="Email attribute"
                            extra={
                                <>
                                    Name of the attribute that contains the email of the user. Defaults to <code>email</code>.
                                </>
                            }
                        >
                            <Input style={{width: '12em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="groupAttribute"
                            label="Group attribute"
                            extra={
                                <>
                                    Name of the attribute that contains the groups the user belongs to. Defaults to <code>memberOf</code>.
                                </>
                            }
                        >
                            <Input style={{width: '12em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="groupFilter"
                            label="Group filter"
                            extra={
                                <>
                                    Name of the <code>OU</code> field used to filter groups a user belongs to.
                                </>
                            }
                        >
                            <Input style={{width: '12em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="groupNameAttribute"
                            label="Group name attribute"
                            extra={
                                <>
                                    The ID of the attribute which contains the name for a group. Defaults to <code>cn</code>.
                                </>
                            }
                        >
                            <Input style={{width: '12em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="groupSearchBase"
                            label="Group search base"
                            extra={
                                <>
                                    The base DN from which the search for group membership should be performed.
                                </>
                            }
                        >
                            <Input style={{width: '12em'}}/>
                        </Form.Item>
                        <Form.Item
                            name="groupSearchFilter"
                            label="Group search filter"
                            extra={
                                <>
                                    The pattern to be used for the user search. <code>{'{'}0{'}'}</code> is the user's DN. Default to
                                    <code>(member={'{'}0{'}'})</code>.
                                </>
                            }
                        >
                            <Input style={{width: '12em'}}/>
                        </Form.Item>
                    </>
                }
            </SettingsForm>
        </>
    )
}