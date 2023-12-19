import {Dynamic} from "@components/common/Dynamic";
import Section from "@components/common/Section";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";

export default function SettingsWrapper({entryId}) {

    const client = useGraphQLClient()

    const [entry, setEntry] = useState()

    useEffect(() => {
        if (client && entryId) {
            client.request(
                gql`
                    query SettingsEntry($id: String!) {
                        settings {
                            settingsById(id: $id) {
                                id
                                title
                                values
                            }
                        }
                    }
                `,
                {id: entryId}
            ).then(data => {
                setEntry(data.settings.settingsById)
            })
        }
    }, [client, entryId])

    return (
        <>
            {
                entry &&
                <Section
                    padding={16}
                    title={entry.title}
                >
                    <Dynamic
                        path={`framework/settings/${entry.id}-form`}
                        props={{...entry.values, id: entry.id}}
                    />
                </Section>
            }
        </>
    )
}