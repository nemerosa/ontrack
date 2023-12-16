import {Button, Form, Input, Popconfirm, Space, Spin, Table} from "antd";
import {useEffect, useState} from "react";
import {FaCog, FaTrash} from "react-icons/fa";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import CheckStatus from "@components/common/CheckStatus";
import TimestampText, {weekDayFormat} from "@components/common/TimestampText";
import {getUserErrors} from "@components/services/graphql-utils";
import FormErrors from "@components/form/FormErrors";
import {Command} from "@components/common/Commands";
import InlineConfirmCommand from "@components/common/InlineConfirmCommand";

const {Column} = Table

export default function UserProfileTokens() {

    const client = useGraphQLClient()

    const [generateTokenForm] = Form.useForm()
    const [generatingToken, setGeneratingToken] = useState(false)
    const [errors, setErrors] = useState([])

    const [tokensReloadCount, setTokensReloadCount] = useState(0)
    const reloadTokens = () => {
        setTokensReloadCount(tokensReloadCount + 1)
    }
    const [tokens, setTokens] = useState([])

    const gqlToken = gql`
        fragment TokenData on Token {
            name
            value
            scope
            creation
            lastUsed
            validUntil
            valid
            transient
        }
    `

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query UserTokens {
                        user {
                            account {
                                id
                                tokens {
                                    ...TokenData
                                }
                            }
                        }
                    }
                    ${gqlToken}
                `
            ).then(data => {
                setTokens(data.user.account.tokens)
            })
        }
    }, [client, tokensReloadCount]);

    const onGenerateToken = ({name}) => {
        if (client && name) {
            setGeneratingToken(true)
            setErrors([])
            client.request(
                gql`
                    mutation GenerateToken($name: String!) {
                        generateToken(input: {name: $name}) {
                            errors {
                                message
                            }
                            token {
                                ...TokenData
                            }
                        }
                    }
                    ${gqlToken}
                `,
                {name}
            ).then(data => {
                const errors = getUserErrors(data.generateToken)
                if (errors) {
                    setErrors(errors)
                } else {
                    reloadTokens()
                }
            }).finally(() => {
                setGeneratingToken(false)
            })
        }
    }

    const onRevokeToken = (token) => {
        client.request(
            gql`
                mutation RevokeToken($name: String!) {
                    revokeToken(input: {name: $name}) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {name: token.name}
        ).then(() => {
            reloadTokens()
        })
    }

    return (
        <>
            <Space direction="vertical" className="ot-line">
                <Form layout="inline" form={generateTokenForm} onFinish={onGenerateToken}>
                    <Form.Item
                        name="name"
                        rules={[{required: true, message: 'Token name is required'}]}
                    >
                        <Input placeholder="Token name"/>
                    </Form.Item>
                    <Form.Item>
                        <Button
                            type="primary"
                            htmlType="submit"
                            disabled={generatingToken}
                            icon={<FaCog/>}
                        >
                            {
                                generatingToken && <Spin/>
                            }
                            Generate token
                        </Button>
                    </Form.Item>
                </Form>
                <FormErrors errors={errors}/>
                <Table
                    dataSource={tokens}
                    pagination={false}
                >
                    <Column
                        key="name"
                        title="Name"
                        dataIndex="name"
                    />
                    <Column
                        key="scope"
                        title="Scope"
                        dataIndex="scope"
                    />
                    <Column
                        key="creation"
                        title="Creation"
                        dataIndex="creation"
                        render={(value) => <TimestampText value={value} format={weekDayFormat}/>}
                    />
                    <Column
                        key="lastUsed"
                        title="Last used"
                        dataIndex="lastUsed"
                        render={(value) => <TimestampText value={value} format={weekDayFormat}/>}
                    />
                    <Column
                        key="validUntil"
                        title="Valid until"
                        dataIndex="validUntil"
                        render={(validUntil, token) => {
                            return <Space>
                                {
                                    validUntil && <TimestampText value={validUntil} format={weekDayFormat}/>
                                }
                                {
                                    validUntil && <CheckStatus
                                        value={token.valid}
                                        text="Valid"
                                        noText="Expired"
                                    />
                                }
                                {
                                    !validUntil && <CheckStatus value={true} text="Does not expire"/>
                                }
                            </Space>
                        }}
                    />
                    <Column
                        key="actions"
                        title=""
                        render={(_, token) =>
                            token.transient ||
                            <InlineConfirmCommand
                                title="Revoking the token"
                                confirm={`Are you sure to revoke the "${token.name}" token?`}
                                onConfirm={() => onRevokeToken(token)}
                            />
                        }
                    />
                </Table>
            </Space>
        </>
    )
}