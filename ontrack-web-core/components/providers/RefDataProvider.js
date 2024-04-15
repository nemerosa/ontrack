import {createContext, useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useJobStates} from "@components/core/admin/jobs/JobState";

const refDataSignature = {
    /**
     * Management of validation run statuses
     */
    validationRunStatuses: {
        /**
         * List of validation run statuses accessible from a given VRS ID
         *
         * @param id ID of the starting validation run status
         * @return Array of VRS objects
         */
        getAccessibleStatuses: (id) => [],
        /**
         * List of all roots
         */
        roots: [],
        /**
         * All possible statuses
         */
        list: [],
    },
    /**
     * List of existing event types (id, description)
     */
    eventTypes: [],
    /**
     * List of jobs states
     */
    jobStates: {
        list: [],
        index: {},
    },
    /**
     * Version of Ontrack
     */
    version: ''
}

export const RefDataContext = createContext(refDataSignature)

export const useRefData = () => useContext(RefDataContext)

export default function RefDataContextProvider({children}) {

    const client = useGraphQLClient()

    const [refData, setRefData] = useState(refDataSignature)

    const validationRunStatuses = (list) => {
        // Indexing all validation run statuses
        const index = {}
        list.forEach(vrs => {
            index[vrs.id] = vrs
        })

        //

        const getAccessibleStatuses = (id) => {
            const vrs = index[id]
            if (vrs) {
                return vrs.followingStatuses.map(fid => index[fid])
            } else {
                return []
            }
        }

        // OK
        return {
            getAccessibleStatuses,
            roots: list.filter(vrs => vrs.root),
            list,
        }
    }

    const jobStates = useJobStates()

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query RefData {
                        validationRunStatusIDList {
                            id
                            name
                            root
                            passed
                            followingStatuses
                        }
                        info {
                            version {
                                display
                            }
                        }
                        eventTypes {
                            id
                            description
                        }
                    }
                `
            ).then(data => {
                setRefData({
                    validationRunStatuses: validationRunStatuses(data.validationRunStatusIDList),
                    eventTypes: data.eventTypes,
                    jobStates: jobStates,
                    version: data.info.version.display,
                })
            })
        }
    }, [client, jobStates]);

    return <RefDataContext.Provider value={refData}>{children}</RefDataContext.Provider>

}