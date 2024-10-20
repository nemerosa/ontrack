import {Button, Space, Table} from "antd";
import {gql} from "graphql-request";
import SlotAdmissionRuleConfigDialog, {
    useSlotAdmissionRuleConfigDialog
} from "@components/extension/environments/SlotAdmissionRuleConfigDialog";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {FaPlus} from "react-icons/fa";
import {isAuthorized} from "@components/common/authorizations";
import SlotAdmissionRuleSummary from "@components/extension/environments/SlotAdmissionRuleSummary";
import SlotAdmissionRuleActions from "@components/extension/environments/SlotAdmissionRuleActions";

export default function SlotAdmissionRulesTable({slot, onChange}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [rules, setRules] = useState([])

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query SlotAdmissionRules($id: String!) {
                        slotById(id: $id) {
                            admissionRules {
                                id
                                name
                                description
                                ruleId
                                ruleConfig
                            }
                        }
                    }
                `,
                {id: slot.id}
            ).then(data => {
                setRules(data.slotById.admissionRules)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, slot])

    const dialog = useSlotAdmissionRuleConfigDialog({
        onSuccess: onChange,
    })

    const addRule = async () => {
        dialog.start({slot})
    }

    return (
        <>
            <SlotAdmissionRuleConfigDialog dialog={dialog}/>
            <Table
                dataSource={rules}
                loading={loading}
                pagination={false}
                footer={() =>
                    <Space>
                        {
                            isAuthorized(slot, "slot", "edit") &&
                            <Button
                                icon={<FaPlus/>}
                                onClick={addRule}
                            >
                                Add admission rule
                            </Button>
                        }
                    </Space>
                }
            >
                <Table.Column
                    key="name"
                    title="Name"
                    dataIndex="name"
                />
                <Table.Column
                    key="description"
                    title="Description"
                    dataIndex="description"
                />
                <Table.Column
                    key="ruleId"
                    title="Rule"
                    render={(_, rule) => <SlotAdmissionRuleSummary ruleId={rule.ruleId} ruleConfig={rule.ruleConfig}/>}
                />
                <Table.Column
                    key="actions"
                    title="Actions"
                    render={(_, rule) => <SlotAdmissionRuleActions id={rule.id} onChange={onChange}/>}
                />
            </Table>
        </>
    )
}