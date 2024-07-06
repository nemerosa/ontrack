import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";

export const useJiraConfigurationUrl = (configName) => {

    const client = useGraphQLClient()
    const [url, setUrl] = useState('')

    useEffect(() => {
        if (client && configName) {
            client.request(
                gql`
                    query GetJiraConfiguration($config: String!) {
                        jiraConfiguration(name: $config) {
                            url
                        }
                    }
                `,
                {config: configName}
            ).then(data => {
                setUrl(data.jiraConfiguration?.url)
            })
        }
    }, [client, configName]);

    return url
}