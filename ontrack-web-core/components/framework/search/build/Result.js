import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import BranchLink from "@components/branches/BranchLink";
import {Space, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";
import BuildLink from "@components/builds/BuildLink";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {gqlDecorationFragment} from "@components/services/fragments";
import Decorations from "@components/framework/decorations/Decorations";

export default function Result({data}) {

    const client = useGraphQLClient()
    const [build, setBuild] = useState({decorations: []})

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query BuildDecorations($id: Int!) {
                        build(id: $id) {
                            decorations {
                                ...decorationContent
                            }
                        }
                    }
                    ${gqlDecorationFragment}
                `,
                {id: data.build.id}
            ).then(data => setBuild(data.build))
        }
    }, [client])

    return <SearchResultComponent
        title={
            <>
                <Space>
                    <ProjectLink project={data.build.branch.project}/>
                    <Typography.Text type="secondary">/</Typography.Text>
                    <BranchLink branch={data.build.branch}/>
                    <Typography.Text type="secondary">/</Typography.Text>
                    <BuildLink build={data.build}/>
                    <Decorations entity={{build}}/>
                </Space>
            </>
        }
        description={data.build.description}
    />
}