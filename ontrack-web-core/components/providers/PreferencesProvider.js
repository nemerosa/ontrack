import {createContext, useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export const PreferencesContext = createContext({
    branchViewVsGroups: null,
    branchViewVsNames: null,
    dashboardUuid: null,
    selectedBranchViewKey: null,
    setPreferences: () => {
    },
})

export default function PreferencesContextProvider({children}) {

    const client = useGraphQLClient()

    const [preferencesRecord, setPreferencesRecord] = useState({})

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query GetPreferences {
                        preferences {
                            branchViewVsGroups
                            branchViewVsNames
                            dashboardUuid
                            selectedBranchViewKey
                        }
                    }
                `
            ).then(data => {
                setPreferencesRecord(data.preferences)
            })
        }
    }, [client]);

    const setPreferences = (values) => {
        client.request(
            gql`
                mutation SetPreferences($input: SetPreferencesInput!) {
                    setPreferences(input: $input) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {input: values}
        ).then(() => {
            setPreferencesRecord({
                ...preferencesRecord,
                ...values,
            })
        })
    }

    const contextValue = {
        ...preferencesRecord,
        setPreferences,
    }

    return <PreferencesContext.Provider value={contextValue}>{children}</PreferencesContext.Provider>
}

export function usePreferences() {
    return useContext(PreferencesContext)
}