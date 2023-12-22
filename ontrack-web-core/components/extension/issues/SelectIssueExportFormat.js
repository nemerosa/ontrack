import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Select} from "antd";

export const useIssueExportFormats = () => {
    const client = useGraphQLClient()
    const [formats, setFormats] = useState([])

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query IssueExportFormats {
                        issueExportFormats {
                            id
                            name
                        }
                    }
                `
            ).then(data => {
                setFormats(data.issueExportFormats)
            })
        }
    }, [client]);

    return formats
}

export default function SelectIssueExportFormat({value, onChange}) {

    const exportFormats = useIssueExportFormats()
    const [exportFormatOptions, setExportFormatOptions] = useState([])
    useEffect(() => {
        setExportFormatOptions(exportFormats.map(format => ({
            value: format.id,
            label: format.name,
        })))
    }, [exportFormats]);

    return (
        <>
            <Select
                value={value}
                onChange={onChange}
                options={exportFormatOptions}
            />
        </>
    )
}