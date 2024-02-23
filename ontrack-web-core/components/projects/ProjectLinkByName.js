import {useEffect, useState} from "react";
import {Space, Spin} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import ProjectLink from "@components/projects/ProjectLink";

export default function ProjectLinkByName({name}) {

    const [loading, setLoading] = useState(true)

    const client = useGraphQLClient()
    const [project, setProject] = useState()

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query ProjectByName($name: String!) {
                        projects(name: $name) {
                            id
                            name
                        }
                    }
                `,
                {name}
            ).then(data => {
                setProject(data.projects[0])
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [name, client]);

    return (
        <>
            {
                !project &&
                <Space>
                    {
                        loading &&
                        <Spin size="small" title="Loading link to project"/>
                    }
                    {name}
                </Space>
            }
            {
                project &&
                <ProjectLink project={project}/>
            }
        </>
    )

}