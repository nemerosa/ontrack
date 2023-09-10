import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {Select, Space, Typography} from "antd";
import ColorBox from "@components/common/ColorBox";

export default function SelectLabel({value, onChange}) {

    const [labels, setLabels] = useState([])

    useEffect(() => {
        graphQLCall(
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
    }, []);

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