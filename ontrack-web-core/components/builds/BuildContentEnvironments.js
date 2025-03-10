import GridCell from "@components/grid/GridCell";
import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {
    gqlSlotDataNoProject,
    gqlSlotPipelineBuildData,
    gqlSlotPipelineDataNoBuild
} from "@components/extension/environments/EnvironmentGraphQL";
import React, {useEffect, useState} from "react";
import {Timeline} from "antd";

export default function BuildContentEnvironments({build}) {

    const {data, loading} = useQuery(
        gql`
            ${gqlSlotPipelineDataNoBuild}
            ${gqlSlotDataNoProject}
            ${gqlSlotPipelineBuildData}
            query BuildEnvironments(
                $id: Int!,
            ) {
                build(id: $id) {
                    slots {
                        ...SlotDataNoProject
                        lastDeployedPipeline {
                            build {
                                ...SlotPipelineBuildData
                            }
                        }
                        pipelines(buildId: $id) {
                            pageItems {
                                ...SlotPipelineDataNoBuild
                            }
                        }
                    }
                }
            }
        `,
        {
            variables: {id: build.id},
        }
    )

    const [items, setItems] = useState([])
    useEffect(() => {
        if (data) {
            const items = []
            const slots = data.build?.slots
            slots.forEach(slot => {
                // Slot node with current build (if != current build)
                const lastDeployment = slot.lastDeployedPipeline
                items.push({
                    label: "Slot",
                })
                // List of deployments
                const deployments = slot.pipelines.pageItems
                deployments.forEach(deployment => {
                    items.push({
                        label: "Deployment"
                    })
                })
            })
            setItems(items)
        }
    }, [data])

    return (
        <>
            <GridCell id="environments" title="Environments" loading={loading} padding={true}>
                <Timeline
                    mode="right"
                    items={items}
                />
            </GridCell>
        </>
    )
}