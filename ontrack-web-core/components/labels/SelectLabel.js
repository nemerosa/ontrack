import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Select, Space, Typography} from "antd";
import ColorBox from "@components/common/ColorBox";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function SelectLabel({value, onChange}) {

    const client = useGraphQLClient()

    const [labels, setLabels] = useState([])

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query GetLabels {
                        labels {
                            id
                            category
                            name
                            title: description
                            color
                        }
                    }
                `
            ).then(data => {
                setLabels(data.labels.map(label => (
                    {
                        value: `${label.category}:${label.name}`,
                        label: <Space>
                            {
                                label.color && <ColorBox color={label.color}/>
                            }
                            <Typography.Text>
                                {label.category && `${label.category} / `}
                                {label.name}
                            </Typography.Text>
                        </Space>,
                        ...label,
                    }
                )))
            })
        }
    }, [client]);

    return (
        <>
            <Select
                value={value}
                onChange={onChange}
                options={labels}
                allowClear={true}
            />
        </>
    )
}