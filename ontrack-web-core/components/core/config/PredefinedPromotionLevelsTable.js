import {gql} from "graphql-request";
import {Form, Input, Space, Table, Typography} from "antd";
import {useState} from "react";
import {useQuery} from "@components/services/useQuery";
import PredefinedPromotionLevelImage from "@components/core/config/PredefinedPromotionLevelImage";
import FilterForm from "@components/common/table/FilterForm";
import PredefinedPromotionLevelUpdateCommand from "@components/core/config/PredefinedPromotionLevelUpdateCommand";
import {useReloadState} from "@components/common/StateUtils";
import PredefinedPromotionLevelChangeImageCommand
    from "@components/core/config/PredefinedPromotionLevelChangeImageCommand";
import PredefinedPromotionLevelDeleteCommand from "@components/core/config/PredefinedPromotionLevelDeleteCommand";
import {DndContext, PointerSensor, useSensor, useSensors} from "@dnd-kit/core";
import {arrayMove, SortableContext, verticalListSortingStrategy} from "@dnd-kit/sortable";
import {restrictToVerticalAxis} from "@dnd-kit/modifiers";
import {DnDRow} from "@components/common/table/DndRow";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function PredefinedPromotionLevelsTable({reloadState}) {

    const client = useGraphQLClient()
    const [changed, onChange] = useReloadState()

    const [filterFormData, setFilterFormData] = useState({
        name: ''
    })
    const {data, setData, loading} = useQuery(
        gql`
            query PredefinedPromotionLevels($name: String = null) {
                predefinedPromotionLevels(name: $name) {
                    key: id
                    id
                    name
                    description
                    isImage
                }
            }
        `,
        {
            variables: filterFormData,
            deps: [filterFormData, reloadState, changed]
        }
    )

    const sensors = useSensors(
        useSensor(PointerSensor, {
            activationConstraint: {
                // https://docs.dndkit.com/api-documentation/sensors/pointer#activation-constraints
                distance: 1,
            },
        }),
    )

    const [reordering, setReordering] = useState(false)

    const reorder = (activeId, overId) => {
        setReordering(true)
        client.request(
            gql`
                mutation ReorderPredefinedPromotionLevels(
                    $activeId: Int!,
                    $overId: Int!,
                ) {
                    reorderPredefinedPromotionLevelById(input: {
                        activeId: $activeId,
                        overId: $overId,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                activeId,
                overId,
            }
        ).then(() => {
            // onChange() not reloading
            // only locally:
            setData(data => {
                const entries = data.predefinedPromotionLevels
                const activeIndex = entries.findIndex((i) => i.id === activeId)
                const overIndex = entries.findIndex((i) => i.id === overId)
                return {
                    ...data,
                    predefinedPromotionLevels: arrayMove(entries, activeIndex, overIndex),
                }
            })
        }).finally(() => {
            setReordering(false)
        })
    }

    const onDragEnd = ({active, over}) => {
        if (active?.id && over?.id && active.id !== over.id) {
            reorder(active.id, over.id)
        }
    };

    return (
        <>
            <FilterForm
                setFilterFormData={setFilterFormData}
                filterForm={[
                    <Form.Item
                        key="name"
                        name="name"
                        label="Name"
                    >
                        <Input style={{width: "15em"}}/>
                    </Form.Item>
                ]}
            />
            <DndContext sensors={sensors} modifiers={[restrictToVerticalAxis]} onDragEnd={onDragEnd}>
                <SortableContext
                    items={data?.predefinedPromotionLevels?.map(i => i.key) ?? []}
                    strategy={verticalListSortingStrategy}
                >
                    <Table
                        loading={loading}
                        dataSource={data?.predefinedPromotionLevels}
                        pagination={false}
                        components={{
                            body: {
                                row: DnDRow,
                            },
                        }}
                        rowKey="key"
                    >
                        <Table.Column
                            key="name"
                            title="Name"
                            render={(_, record) => <Space>
                                <code>{record.key}</code>
                                <PredefinedPromotionLevelImage
                                    predefinedPromotionLevel={record}
                                />
                                <Typography.Text>{record.name}</Typography.Text>
                            </Space>}
                        />
                        <Table.Column
                            key="description"
                            title="Description"
                            render={(_, record) => <Typography.Text
                                type="secondary">{record.description}</Typography.Text>}
                        />
                        <Table.Column
                            key="actions"
                            title="Actions"
                            render={(_, record) =>
                                <Space>
                                    <PredefinedPromotionLevelUpdateCommand ppl={record} onChange={onChange}/>
                                    <PredefinedPromotionLevelChangeImageCommand id={record.id} onChange={onChange}/>
                                    <PredefinedPromotionLevelDeleteCommand ppl={record} onChange={onChange}/>
                                </Space>
                            }
                        />
                    </Table>
                </SortableContext>
            </DndContext>
        </>
    )
}