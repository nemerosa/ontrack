import {createContext, useContext, useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

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
    }
}

export const RefDataContext = createContext(refDataSignature)

export const useRefData = () => useContext(RefDataContext)

export default function RefDataContextProvider({children}) {

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
        }
    }

    useEffect(() => {
        graphQLCall(
            gql`
                query RefData {
                    validationRunStatusIDList {
                        id
                        name
                        root
                        passed
                        followingStatuses
                    }
                }
            `
        ).then(data => {
            setRefData({
                validationRunStatuses: validationRunStatuses(data.validationRunStatusIDList),
            })
        })
    }, []);

    return <RefDataContext.Provider value={refData}>{children}</RefDataContext.Provider>

}