import {createContext, useContext, useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";

export const WorkflowNodeExecutorContext = createContext([])

export const useWorkflowNodeExecutor = (executorId, dependencies = []) => {
    const [executor, setExecutor] = useState({})
    const context = useContext(WorkflowNodeExecutorContext)
    useEffect(() => {
        if (executorId && context) {
            setExecutor(context.find(it => it.id === executorId))
        }
    }, [dependencies, executorId, context]);
    return executor
}

export default function WorkflowNodeExecutorContextProvider({children}) {

    const client = useGraphQLClient()
    const [executors, setExecutors] = useState([])

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query WorkflowNodeExecutors {
                        workflowNodeExecutors(enabled: true) {
                            id
                            displayName
                        }
                    }
                `
            ).then(data => {
                setExecutors(data.workflowNodeExecutors)
            })
        }
    }, [client]);

    return (
        <>
            <WorkflowNodeExecutorContext.Provider value={executors}>
                {children}
            </WorkflowNodeExecutorContext.Provider>
        </>
    )
}