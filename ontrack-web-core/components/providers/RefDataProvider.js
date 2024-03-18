import {createContext, useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

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
                    }
                `
            ).then(data => {
                setRefData({
                    validationRunStatuses: validationRunStatuses(data.validationRunStatusIDList),
                    version: data.info.version.display,
                })
            })
        }
    }, [client]);

    return <RefDataContext.Provider value={refData}>{children}</RefDataContext.Provider>

}