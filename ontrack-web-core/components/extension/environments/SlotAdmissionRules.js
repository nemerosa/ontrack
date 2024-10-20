import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";

export const useSlotAdmissionRules = () => {
    const client = useGraphQLClient()

    const [rules, setRules] = useState([])

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query SlotAdmissionRules {
                        slotAdmissionRules {
                            id
                            name
                        }
                    }
                `
            ).then(data => {
                setRules(data.slotAdmissionRules)
            })
        }
    }, [client])

    return rules
}