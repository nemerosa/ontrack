import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect} from "react";
import {gql} from "graphql-request";

export default function SlotEligibleBuild({slot}) {
    const client = useGraphQLClient()

    useEffect(() => {
        if (client && slot) {
            client.request(
                gql`
                    query SlotEligibleBuild($id: String!) {
                        slotById(id: $id) {
                            eligibleBuild {
                                id
                                name
                                creation {
                                    time
                                }
                                branch {
                                    id
                                    name
                                    project {
                                        id
                                        name
                                    }
                                }
                                promotionRuns(lastPerLevel: true) {
                                    id
                                    creation {
                                        time
                                    }
                                    promotionLevel {
                                        id
                                        name
                                        description
                                        image
                                        _image
                                    }
                                }
                                releaseProperty {
                                    value
                                }
                            }
                        }
                    }
                `,
                {
                    id: slot.id,
                }
            ).then(data => {

            }).finally(() => {

            })
        }
    }, [client, slot])
}