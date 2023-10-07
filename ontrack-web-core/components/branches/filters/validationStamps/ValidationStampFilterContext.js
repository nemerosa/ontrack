import {createContext, useEffect, useReducer} from "react";
import graphQLCall from "@client/graphQLCall";
import {
    gqlValidationStampFilterFragment
} from "@components/branches/filters/validationStamps/ValidationStampFilterGraphQLFragments";
import {gql} from "graphql-request";

const ValidationStampFilterContext = createContext(null)

const ValidationStampFilterDispatchContext = createContext(null)

export function ValidationStampFilterProvider({branch, children}) {
    const [filters, dispatch] = useReducer(
        validationStampFilterReducer,
        [],
        undefined
    )

    useEffect(() => {
        graphQLCall(
            gql`
                query GetValidationStampFilters(
                    $branchId: Int!,
                ) {
                    branch(id: $branchId) {
                        validationStampFilters(all: true) {
                            ...validationStampFilterContent
                        }
                    }
                }

                ${gqlValidationStampFilterFragment}
            `, {
                branchId: branch.id,
            }
        ).then(data => {
            dispatch({
                type: 'init',
                filters: data.branch.validationStampFilters
            })
        })
    }, [branch]);

    return (
        <ValidationStampFilterContext.Provider value={filters}>
            <ValidationStampFilterDispatchContext.Provider value={dispatch}>
                {children}
            </ValidationStampFilterDispatchContext.Provider>
        </ValidationStampFilterContext.Provider>
    )
}

function validationStampFilterReducer(filters, action) {
    switch (action.type) {
        case 'init': {
            return action.filters
        }
        default: {
            throw Error(`Unknown action: ${action.type}`)
        }
    }
}
