import {Button, Form, Input, Space, Spin} from "antd";
import CheckIcon from "@components/common/CheckIcon";
import FormErrors from "@components/form/FormErrors";
import {useState} from "react";
import {gql} from "graphql-request";
import {getUserErrors} from "@components/services/graphql-utils";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function UserProfileChangePassword() {

    const client = useGraphQLClient()

    const [changingPassword, setChangingPassword] = useState(false)
    const [errors, setErrors] = useState([])
    const [changedPassword, setChangedPassword] = useState(false)

    const [passwordForm] = Form.useForm()

    const clearPasswordForm = () => {
        passwordForm.resetFields()
    }

    const onChangePassword = (values) => {
        setChangingPassword(true)
        setErrors([])
        client.request(
            gql`
                mutation ChangePassword($oldPassword: String!, $newPassword: String!) {
                    changePassword(input: {
                        oldPassword: $oldPassword,
                        newPassword: $newPassword,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            values
        ).then(data => {
            const errors = getUserErrors(data.changePassword)
            if (errors) {
                setErrors(errors)
            } else {
                passwordForm.resetFields()
                setChangedPassword(true)
            }
        }).finally(() => {
            setChangingPassword(false)
        })
    }

    return (
        <>
                <Form
                    form={passwordForm}
                    labelCol={{span: 4}}
                    wrapperCol={{span: 5}}
                    onFinish={onChangePassword}
                >
                    <Form.Item
                        name="oldPassword"
                        label="Old password"
                        rules={[{required: true, message: 'Old password is required'}]}
                    >
                        <Input.Password/>
                    </Form.Item>
                    <Form.Item
                        name="newPassword"
                        label="New password"
                        rules={[{required: true, message: 'New password is required'}]}
                    >
                        <Input.Password/>
                    </Form.Item>
                    <Form.Item
                        name="confirmPassword"
                        label="Confirm password"
                        rules={[
                            {required: true, message: 'Password confirmation is required'},
                            ({getFieldValue}) => ({
                                validator(_, value) {
                                    if (!value || getFieldValue('newPassword') === value) {
                                        return Promise.resolve()
                                    }
                                    return Promise.reject(new Error('Password confirmation must match'));
                                },
                            }),
                        ]}
                    >
                        <Input.Password/>
                    </Form.Item>
                    <Form.Item style={{textAlign: 'right'}}>
                        <Space>
                            <Button
                                type="primary"
                                htmlType="submit"
                                disabled={changingPassword}
                            >
                                {
                                    changingPassword && <Spin/>
                                }
                                Submit
                            </Button>
                            <Button
                                type="default"
                                disabled={changingPassword}
                                onClick={clearPasswordForm}
                            >
                                Clear
                            </Button>
                            {
                                changedPassword &&
                                <>
                                    <CheckIcon value={true}/>
                                    Password changed
                                </>
                            }
                        </Space>
                    </Form.Item>
                </Form>
                <FormErrors errors={errors}/>
        </>
    )
}