import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useEffect, useState} from "react";
import {Select} from "antd";

export default function SelectAutoVersioningAuditState({value, onChange}) {

    const client = useGraphQLClient()

    const [states, setStates] = useState([])
    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query AutoVersioningAuditStates {
                        autoVersioningAuditStates
                    }
                `
            ).then(data => {
                setStates(
                    data.autoVersioningAuditStates.map(id => ({
                        value: id,
                        label: id,
                    }))
                )
            })
        }
    }, [client]);

    return (
        <>
            <Select
                options={states}
                value={value}
                onChange={onChange}
                style={{
                    width: '20em',
                }}
            />
        </>
    )
}