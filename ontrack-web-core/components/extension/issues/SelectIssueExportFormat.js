import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Select} from "antd";

export const useTemplateRenderers = () => {
    const client = useGraphQLClient()
    const [templateRenderers, setTemplateRenderers] = useState([])

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query TemplatingRenderers {
                        templatingRenderers {
                            id
                            name
                        }
                    }
                `
            ).then(data => {
                setTemplateRenderers(data.templatingRenderers)
            })
        }
    }, [client]);

    return templateRenderers
}

export default function SelectIssueExportFormat({value, onChange}) {

    const exportFormats = useTemplateRenderers()
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