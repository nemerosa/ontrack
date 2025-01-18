import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import LoadingContainer from "@components/common/LoadingContainer";
import {Tree} from "antd";
import CascSchemaNode from "@components/extension/casc/CascSchemaNode";

const isScalar = (type) => {
    return type.__type !== 'object' && type.__type !== 'array'
}

const typeToTreeChildren = (type) => {
    const __type = type.__type
    switch (__type) {
        case "object":
            return type.fields.map(child => fieldToTreeNode(child))
        case "array":
            if (isScalar(type.type)) {
                return [
                    {
                        title: <CascSchemaNode name="-" description={type.type.description}/>,
                        children: [],
                    }
                ]
            } else {
                return [
                    {
                        title: <CascSchemaNode name="-" description={type.type.description}/>,
                        children: typeToTreeChildren(type.type),
                    }
                ]
            }
        default:
            return []
    }
}

const fieldToTreeNode = (field) => {
    let type = ""
    if (isScalar(field.type)) {
        type = field.type.description
    }
    return {
        title: <CascSchemaNode name={field.name} type={type} description={field.description} required={field.required}/>,
        children: typeToTreeChildren(field.type),
    }
}

const toTreeData = (schema) => {
    return schema.fields.map(field => fieldToTreeNode(field))
}

export default function CascSchema() {

    const {data: treeData, loading} = useQuery(
        gql`
            query CascSchema {
                casc {
                    schema
                }
            }
        `,
        {
            dataFn: data => toTreeData(data.casc.schema)
        }
    )

    return (
        <>
            <LoadingContainer loading={loading}>
                <Tree
                    treeData={treeData}
                    defaultExpandAll={true}
                    selectable={false}
                />
            </LoadingContainer>
        </>
    )

}