// noinspection JSUnusedGlobalSymbols

import Widget from "@components/dashboards/widgets/Widget";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import ProjectBox from "@components/projects/ProjectBox";
import {Space} from "antd";

export default function LastActiveProjectsWidget({count}) {

    const [loading, setLoading] = useState(true)
    const [projects, setProjects] = useState([])

    useEffect(() => {
        if (count) {
            setLoading(true)
            graphQLCall(
                gql`
                    query LastActiveProjects($count: Int! = 10) {
                        lastActiveProjects(count: $count) {
                            id
                            name
                            favourite
                        }
                    }
                `
            ).then(data => {
                setProjects(data.lastActiveProjects)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [count])

    return (
        // TODO Loading indicator for the widget
        <Widget title={`Last ${count} active projects`}>
            <Space direction="horizontal" size={16} wrap>
                {projects.map(project => <ProjectBox key={project.id} project={project}/>)}
            </Space>
        </Widget>
    )
}