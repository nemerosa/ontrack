import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {useContext, useState} from "react";
import {UserContext} from "@components/providers/UserProvider";

export const useProjectBuildSearch = ({project}) => {

    const [values, setValues] = useState({})

    const user = useContext(UserContext)

    const {data: builds, setData: setBuilds, loading, refetch} = useQuery(
        gql`
            query ProjectBuildSearch(
                $projectName: String!,
                $filter: BuildSearchForm,
            ) {
                builds(
                    project: $projectName,
                    buildProjectFilter: $filter,
                ) {
                    id
                    name
                    releaseProperty {
                        value
                    }
                    branch {
                        id
                        name
                        displayName
                    }
                    promotionRuns {
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
                }
            }
        `,
        {
            skipInitialFetch: true,
            variables: {
                projectName: project?.name,
                filter: {
                    ...values,
                    buildExactMatch: true,
                },
            },
            deps: [project?.name, values],
            dataFn: data => data.builds,
        }
    )

    const search = (values) => {
        const extensions = []
        if (user.authorizations.environment?.view && values.environmentName) {
            extensions.push({
                extension: "environment",
                value: values.environmentName,
            })
            delete values.environmentName
        }
        setValues({
            ...values,
            extensions,
        })
        refetch()
    }

    return {
        builds,
        setBuilds,
        loading,
        search,
    }
}