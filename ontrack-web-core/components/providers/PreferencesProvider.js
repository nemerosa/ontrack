import {createContext, useContext, useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export const PreferencesContext = createContext({
    branchViewVsGroups: null,
    branchViewVsNames: null,
    dashboardUuid: null,
    selectedBranchViewKey: null,
    setPreferences: () => {
    },
})

export default function PreferencesContextProvider({children}) {

    const [preferencesRecord, setPreferencesRecord] = useState({})

    useEffect(() => {
        graphQLCall(
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
    }, []);

    const setPreferences = (values) => {
        graphQLCall(
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