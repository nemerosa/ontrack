import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Space, Spin, Tooltip} from "antd";
import Link from "next/link";
import {useEffect, useState} from "react";
import {FaLevelUpAlt} from "react-icons/fa";

export default function AutoVersioningAuditProjectSourceLink({name}) {

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
                <Tooltip title={`Auto-versioning audit for project source ${name}`}>
                    <Link href={`/extension/auto-versioning/audit-project-source/${project.id}`}>
                        <FaLevelUpAlt/>
                    </Link>
                </Tooltip>
            }
        </>
    )
}