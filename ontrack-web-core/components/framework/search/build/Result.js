import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import {Space} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {gqlDecorationFragment} from "@components/services/fragments";
import Decorations from "@components/framework/decorations/Decorations";
import FQBuildLink from "@components/builds/FQBuildLink";

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
                    <FQBuildLink build={data.build}/>
                    <Decorations entity={{build}}/>
                </Space>
            </>
        }
        description={data.build.description}
    />
}