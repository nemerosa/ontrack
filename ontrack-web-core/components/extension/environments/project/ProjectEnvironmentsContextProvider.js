import {createContext, useContext, useEffect, useState} from "react";
import {useProject} from "@components/services/useProject";
import LoadingContainer from "@components/common/LoadingContainer";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {gqlSlotData, gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import {useReloadState} from "@components/common/StateUtils";

export const ProjectEnvironmentsContext = createContext({
    project: null,
    qualifier: "",
    loadingBuilds: false,
    builds: [],
    selectedBuild: null,
    setSelectedBuild: () => {
    },
    selectedSlot: null,
    setSelectedSlot: () => {
    },
    allEligible: null,
    setAllEligible: () => {
    },
    currentDeployment: null,
    currentDeploymentLoading: false,
    onDeploymentAction: () => {
    }
})

export const useProjectEnvironmentsContext = () => useContext(ProjectEnvironmentsContext)

export default function ProjectEnvironmentsContextProvider({id, qualifier = "", children}) {

    const client = useGraphQLClient()

    // Project
    const {project, loading} = useProject({id})

    const [loadingBuilds, setLoadingBuilds] = useState(false)

    // List of last builds for the project
    const [lastBuilds, setLastBuilds] = useState([])
    useEffect(() => {
        if (client && project) {
            setLoadingBuilds(true)
            client.request(
                gql`
                    ${gqlSlotPipelineData}
                    ${gqlSlotData}
                    query ProjectLastBuilds(
                        $projectName: String!,
                        $qualifier: String!,
                    ) {
                        builds(project: $projectName, buildProjectFilter: {maximumCount: 10}) {
                            ...SlotPipelineBuildData
                            deployed: currentDeployments(qualifier: $qualifier) {
                                ...SlotPipelineData
                                slot {
                                    ...SlotData
                                }
                            }
                        }
                    }
                `,
                {
                    projectName: project.name,
                    qualifier,
                }
            ).then(data => {
                setLastBuilds(data.builds)
            }).finally(() => {
                setLoadingBuilds(false)
            })
        }
    }, [client, project, qualifier])

    // Slot selection
    const [selectedSlot, setSelectedSlot] = useState(null)

    // List of builds for the slot
    const [slotBuilds, setSlotBuilds] = useState([])
    const [allEligible, setAllEligible] = useState(false)
    useEffect(() => {
        if (client && project) {
            if (selectedSlot) {
                setLoadingBuilds(true)
                client.request(
                    gql`
                        ${gqlSlotPipelineData}
                        ${gqlSlotData}
                        query SlotBuilds(
                            $slotId: String!,
                            $qualifier: String!,
                            $deployable: Boolean!,
                        ) {
                            slotById(id: $slotId) {
                                eligibleBuilds(deployable: $deployable, size: 10) {
                                    pageItems {
                                        ...SlotPipelineBuildData
                                        deployed: currentDeployments(qualifier: $qualifier) {
                                            ...SlotPipelineData
                                            slot {
                                                ...SlotData
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    `,
                    {
                        projectName: project.name,
                        qualifier,
                        slotId: selectedSlot.id,
                        deployable: !allEligible,
                    }
                ).then(data => {
                    setSlotBuilds(data.slotById?.eligibleBuilds?.pageItems ?? [])
                }).finally(() => {
                    setLoadingBuilds(false)
                })
            } else {
                setSlotBuilds([])
            }
        }
    }, [client, project, qualifier, selectedSlot, allEligible])

    // List of builds to display
    const [builds, setBuilds] = useState([])
    useEffect(() => {
        if (selectedSlot) {
            setBuilds(slotBuilds)
        } else {
            setBuilds([...lastBuilds])
        }
    }, [lastBuilds, slotBuilds, selectedSlot])

    // Build selection
    const [selectedBuild, setSelectedBuild] = useState(null)

    // Current deployment
    const [currentDeployment, setCurrentDeployment] = useState(null)
    const [currentDeploymentLoading, setCurrentDeploymentLoading] = useState(false)
    const [deploymentState, onDeploymentAction] = useReloadState()
    useEffect(() => {
        if (client && selectedBuild && selectedSlot) {
            setCurrentDeploymentLoading(true)
            client.request(
                gql`
                    query CurrentDeployment(
                        $slotId: String!
                    ) {
                        slotById(id: $slotId) {
                            currentPipeline {
                                id
                                status
                                build {
                                    id
                                }
                            }
                        }
                    }
                `,
                {
                    slotId: selectedSlot.id,
                }
            ).then(data => {
                const current = data.slotById?.currentPipeline
                if (current && current.build.id === selectedBuild.id) {
                    setCurrentDeployment(current)
                } else {
                    setCurrentDeployment(null)
                }
            }).finally(() => {
                setCurrentDeploymentLoading(false)
            })
        } else {
            setCurrentDeployment(null)
        }
    }, [client, selectedBuild, selectedSlot, deploymentState])

    // Context
    const context = {
        project,
        qualifier,
        loadingBuilds,
        builds,
        selectedBuild,
        setSelectedBuild,
        selectedSlot,
        setSelectedSlot,
        allEligible,
        setAllEligible,
        currentDeployment,
        currentDeploymentLoading,
        onDeploymentAction,
    }

    return (
        <>
            <ProjectEnvironmentsContext.Provider value={context}>
                <LoadingContainer loading={loading}>
                    {
                        project && children
                    }
                </LoadingContainer>
            </ProjectEnvironmentsContext.Provider>
        </>
    )

}