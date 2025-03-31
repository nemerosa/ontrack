import {createContext, useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {useMutation, useQuery} from "@components/services/GraphQL";
import LoadingContainer from "@components/common/LoadingContainer";

export const PreferencesContext = createContext({
    branchViewVsGroups: null,
    branchViewVsNames: null,
    dashboardUuid: null,
    selectedBranchViewKey: null,
    setPreferences: () => {
    },
})

export default function PreferencesContextProvider({children}) {

    const [preferencesRecord, setPreferencesRecord] = useState({
        branchViewVsGroups: null,
        branchViewVsNames: null,
        dashboardUuid: null,
        selectedBranchViewKey: null,
    })

    const {data, loading, error, finished} = useQuery(
        gql`
            query GetPreferences {
                preferences {
                    branchViewVsGroups
                    branchViewVsNames
                    dashboardUuid
                    selectedBranchViewKey
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
        setPreferencesRecord({
            ...preferencesRecord,
            ...values,
        })
    }

    const contextValue = {
        ...preferencesRecord,
        setPreferences,
    }

    return <LoadingContainer loading={loading} error={error}>
        <PreferencesContext.Provider value={contextValue}>{children}</PreferencesContext.Provider>
    </LoadingContainer>
}

export function usePreferences() {
    return useContext(PreferencesContext)
}