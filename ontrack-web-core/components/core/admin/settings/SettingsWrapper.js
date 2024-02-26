import {Dynamic} from "@components/common/Dynamic";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import PageSection from "@components/common/PageSection";

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
                <PageSection
                    title={entry.title}
                >
                    <Dynamic
                        path={`framework/settings/${entry.id}-form`}
                        props={{...entry.values, id: entry.id}}
                    />
                </PageSection>
            }
        </>
    )
}