import {createContext, useContext, useEffect, useState} from "react";
import {useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";
import {useJobStates} from "@components/core/admin/jobs/JobState";

const refDataSignature = {
    /**
     * Ref data loaded?
     */
    loaded: false,
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
     * List of search result types
     */
    searchResultTypes: [],
    /**
     * Version of Ontrack
     */
    version: ''
}

export const RefDataContext = createContext(refDataSignature)

export const useRefData = () => useContext(RefDataContext)

export default function RefDataContextProvider({children}) {

    const {data, loading, error, finished} = useQuery(
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
                searchResultTypes {
                    feature {
                        id
                    }
                    id
                    name
                    description
                }
            }
        `
    )


    const jobStates = useJobStates()
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

    useEffect(() => {
        if (data && finished) {
            setRefData({
                loaded: true,
                validationRunStatuses: validationRunStatuses(data.validationRunStatusIDList),
                eventTypes: data.eventTypes,
                jobStates: jobStates,
                version: data.info.version.display,
                searchResultTypes: data.searchResultTypes,
            })
        }
    }, [data, finished, jobStates])

    return <>
        {
            !loading &&
            <RefDataContext.Provider value={refData}>{children}</RefDataContext.Provider>
        }
    </>

}