import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input, InputNumber, Switch} from "antd";
import SelectWebhookAuthenticator from "@components/extension/notifications/webhooks/SelectWebhookAuthenticator";
import {useState} from "react";
import Well from "@components/common/Well";
import WebhookAuthenticatorConfig from "@components/extension/notifications/webhooks/WebhookAuthenticatorConfig";
import {gql} from "graphql-request";

export const useWebhookDialog = ({onSuccess}) => {

    const [authenticationType, setAuthenticationType] = useState()
    return useFormDialog({
        onSuccess,
        authenticationType, setAuthenticationType,
        init: (form, {webhook}) => {
            if (webhook) {
                setAuthenticationType(webhook.authenticationType)
                form.setFieldsValue(webhook)
            }
        },
        prepareValues: (values, {webhook}) => {
            const tmpValues = {
                ...values,
                description: values.description ?? '',
                disabled: values.disabled !== null ? values.disabled : false,
            }
            if (webhook) {
                tmpValues.name = webhook.name
            }
            return tmpValues
        },
        query: (context) =>
            context?.creation ?
                gql`
                    mutation CreateWebhook(
                        $name: String!,
                        $enabled: Boolean!,
                        $url: String!,
                        $timeoutSeconds: Long!,
                        $authenticationType: String!,
                        $authenticationConfig: JSON!,
                    ) {
                        createWebhook(input: {
                            name: $name,
                            enabled: $enabled,
                            url: $url,
                            timeoutSeconds: $timeoutSeconds,
                            authenticationType: $authenticationType,
                            authenticationConfig: $authenticationConfig,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                ` :
                gql`
                    mutation UpdateWebhook(
                        $name: String!,
                        $enabled: Boolean!,
                        $url: String!,
                        $timeoutSeconds: Long!,
                        $authenticationType: String!,
                        $authenticationConfig: JSON!,
                    ) {
                        updateWebhook(input: {
                            name: $name,
                            enabled: $enabled,
                            url: $url,
                            timeoutSeconds: $timeoutSeconds,
                            authenticationType: $authenticationType,
                            authenticationConfig: $authenticationConfig,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `,
        userNode: (context) => context?.creation ? 'createWebhook' : 'updateWebhook'
    })
}

export default function WebhookDialog({webhookDialog}) {

    const onSelectedWebhookAuthenticator = (authenticationType) => {
        webhookDialog.setAuthenticationType(authenticationType)
    }

    return (
        <>
            <FormDialog dialog={webhookDialog}>
                <Form.Item name="name"
                           label="Name"
                           extra="Unique name for the webhook"
                           // Readonly if editing an existing webhook
                           rules={[
                               {required: true, message: 'Webhook name is required.',},
                           ]}
                >
                    <Input
                        placeholder="Webhook name"
                        allowClear
                        disabled={!webhookDialog?.context?.creation}
                    />
                </Form.Item>
                <Form.Item name="enabled"
                           label="Enabled"
                           extra="Enabling this webhook"
                >
                    <Switch/>
                </Form.Item>
                <Form.Item name="url"
                           label="URL"
                           extra="Endpoint where to send the webhook payload"
                           rules={[
                               {required: true, message: 'Webhook URL is required.',},
                           ]}
                >
                    <Input placeholder="Webhook URL" allowClear/>
                </Form.Item>
                <Form.Item name="timeoutSeconds"
                           label="Timeout (s)"
                           extra="Timeout (in seconds) for the webhook to be processed"
                           rules={[
                               {required: true, message: 'Timeout is required.',},
                           ]}
                >
                    <InputNumber min={0}/>
                </Form.Item>
                <Form.Item name="authenticationType"
                           label="Authentication type"
                           extra="Type of authentication to use"
                           rules={[
                               {required: true, message: 'Timeout is required.',},
                           ]}
                >
                    <SelectWebhookAuthenticator onSelectedWebhookAuthenticator={onSelectedWebhookAuthenticator}/>
                </Form.Item>
                {
                    webhookDialog?.authenticationType &&
                    <Form.Item
                        label="Authentication configuration"
                    >
                        <Well>
                            <WebhookAuthenticatorConfig
                                prefix="authenticationConfig"
                                authenticationType={webhookDialog?.authenticationType}
                                creation={webhookDialog?.context?.creation}
                            />
                        </Well>
                    </Form.Item>
                }
            </FormDialog>
        </>
    )
}
