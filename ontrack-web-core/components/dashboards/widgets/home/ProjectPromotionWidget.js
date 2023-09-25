import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import ProjectPromotionWidgetForm from "@components/dashboards/widgets/home/ProjectPromotionWidgetForm";
import {gql} from "graphql-request";
import React, {useState} from "react";
import {Popover, Space, Table} from "antd";
import PromotionRun from "@components/promotionRuns/PromotionRun";
import {gqlDecorationFragment} from "@components/services/fragments";
import BuildBox from "@components/builds/BuildBox";
import {FaBan} from "react-icons/fa";
import BuildDependency from "@components/builds/BuildDependency";

const {Column} = Table;

export default function ProjectPromotionWidget({project, promotions, depth, label}) {

    const [runs, setRuns] = useState([])
    const [projects, setProjects] = useState([])

    return (
        <>
            <SimpleWidget
                title={
                    project ? `Promotions for ${project}` : "Project promotions"
                }
                padding={false}
                query={
                    gql`
                        query GetProjectPromotions(
                            $project: String!,
                            $promotions: [String!]!,
                            $depth: Int = 0,
                            $label: String = null,
                        ) {
                            projects(name: $project) {
                                lastBuildsWithPromotions(promotions: $promotions) {
                                    key: id
                                    ...promotionRunContent
                                    build {
                                        ...buildContent
                                        usingQualified(
                                            size: 10,
                                            depth: $depth,
                                            label: $label,
                                        ) {
                                            pageItems {
                                                qualifier
                                                build {
                                                    ...buildContent
                                                    branch {
                                                        project {
                                                            name
                                                        }
                                                    }
                                                }                                        
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        fragment buildContent on Build {
                            id
                            name
                            creation {
                                time
                                user
                            }
                            decorations {
                                ...decorationContent
                            }
                            promotionRuns(lastPerLevel: true) {
                                ...promotionRunContent
                            }
                        }
                        
                        fragment promotionRunContent on PromotionRun {
                            id
                            creation {
                                time
                                user
                            }
                            description
                            annotatedDescription
                            promotionLevel {
                                id
                                name
                                description
                                annotatedDescription
                                image
                            }
                        }
                        
                        ${gqlDecorationFragment}
                    `
                }
                queryDeps={[project, promotions, depth, label]}
                variables={{project, promotions, depth, label}}
                canQuery={() => project}
                setData={data => {
                    const projectList = []
                    data.projects[0].lastBuildsWithPromotions.forEach(run => {
                        run.build.usingQualified.pageItems.forEach(dependency => {
                            const projectName = dependency.build.branch.project.name
                            if (!run.dependencies) {
                                run.dependencies = {}
                            }
                            run.dependencies[projectName] = dependency
                            if (projectList.indexOf(projectName) < 0) {
                                projectList.push(projectName)
                            }
                        })
                    })
                    setProjects(projectList.sort())
                    setRuns(data.projects[0].lastBuildsWithPromotions);
                }}
                form={
                    <ProjectPromotionWidgetForm
                        project={project}
                        promotions={promotions}
                        depth={depth}
                        label={label}
                    />
                }
            >
                <Table
                    dataSource={runs}
                    pagination={false}
                >
                    <Column
                        title="Promotion"
                        render={(_, run) => <PromotionRun
                            promotionRun={run}
                            displayPromotionLevelName={true}
                        />}
                    />
                    <Column
                        title="Last build"
                        render={(_, run) => <Space>
                            <BuildBox
                                build={run.build}
                                creationDisplayMode="tooltip"
                            />
                            <Space size={8}>
                                {
                                    run.build.promotionRuns.map(promotionRun =>
                                        <PromotionRun
                                            key={promotionRun.id}
                                            promotionRun={promotionRun}
                                            size={16}
                                        />
                                    )
                                }
                            </Space>
                        </Space>}
                    />
                    {
                        projects.map(projectName => <Column
                            title={projectName}
                            key={projectName}
                            render={(_, run) => {
                                const dependency = run.dependencies && run.dependencies[projectName]
                                if (dependency) {
                                    return <BuildDependency
                                        link={dependency}
                                        displayPromotions={true}
                                        displayProject={false}
                                    />
                                } else {
                                    return <Popover content={`No dependency on ${projectName}`}>
                                        <FaBan/>
                                    </Popover>
                                }
                            }}
                        />)
                    }
                </Table>
            </SimpleWidget>
        </>
    )
}