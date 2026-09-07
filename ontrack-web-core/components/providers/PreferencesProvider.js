import {createContext, useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {useMutation, useQuery} from "@components/services/GraphQL";

export const PreferencesContext = createContext({
    branchViewVsGroups: null,
    branchViewVsNames: null,
    dashboardUuid: null,
    selectedBranchViewKey: null,
    themeMode: null,
    selectedChangeLogViewKey: null,
    changeLogSemanticFormat: null,
    changeLogSemanticEmojis: null,
    changeLogSemanticIssues: null,
    changeLogSemanticCommits: null,
    setPreferences: () => {
    },
    loaded: false,
})

export default function PreferencesContextProvider({children}) {

    const [loaded, setLoaded] = useState(false)

    const [preferencesRecord, setPreferencesRecord] = useState({
        branchViewVsGroups: null,
        branchViewVsNames: null,
        dashboardUuid: null,
        selectedBranchViewKey: null,
        themeMode: null,
        selectedChangeLogViewKey: null,
        changeLogSemanticFormat: null,
        changeLogSemanticEmojis: null,
        changeLogSemanticIssues: null,
        changeLogSemanticCommits: null,
    })

    const {data, loading, error, finished} = useQuery(
        gql`
            query GetPreferences {
                preferences {
                    branchViewVsGroups
                    branchViewVsNames
                    dashboardUuid
                    selectedBranchViewKey
                    themeMode
                    selectedChangeLogViewKey
                    changeLogSemanticFormat
                    changeLogSemanticEmojis
                    changeLogSemanticIssues
                    changeLogSemanticCommits
                }
            }
        `,
        {
            dataFn: data => data.preferences
        }
    )

    useEffect(() => {
        if (data && finished) {
            setPreferencesRecord(data)
            setLoaded(true)
        }
    }, [data, finished])

    const {mutate} = useMutation(
        gql`
            mutation SetPreferences($input: SetPreferencesInput!) {
                setPreferences(input: $input) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'setPreferences'
        }
    )

    const setPreferences = async (values) => {
        await mutate({input: values})
        // Functional update, not a merge into the record captured by this render: two
        // preferences written in quick succession - the four switches in the change log's
        // semantic panel invite exactly that - would otherwise have the second overwrite the
        // first with the state it saw when it was created.
        setPreferencesRecord(previous => ({
            ...previous,
            ...values,
        }))
    }

    const contextValue = {
        ...preferencesRecord,
        setPreferences,
        loaded,
    }

    return <>
        {
            !loading &&
            <PreferencesContext.Provider value={contextValue}>{children}</PreferencesContext.Provider>
        }
    </>
}

export function usePreferences() {
    return useContext(PreferencesContext)
}