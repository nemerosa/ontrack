import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import Head from "next/head";
import {subBranchTitle} from "@components/common/Titles";
import {downToBranchBreadcrumbs} from "@components/common/Breadcrumbs";
import MainPage from "@components/layouts/MainPage";
import {List, Skeleton} from "antd";
import {gql} from "graphql-request";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";

export default function BranchPromotionLevelsView({id}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [branch, setBranch] = useState({project: {}})
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            client.request(
                gql`
                    query BranchPromotionLevels($id: Int!) {
                        branch(id: $id) {
                            id
                            name
                            project {
                                id
                                name
                            }
                            promotionLevels {
                                id
                                name
                                description
                                image
                                properties {
                                    type {
                                        typeName
                                    }
                                    value
                                }
                            }
                        }
                    }
                `,
                {id}
            ).then(data => {
                setBranch(data.branch)

                const commands = []
                commands.push(
                    <CloseCommand href={branchUri(data.branch)}/>
                )
                setCommands(commands)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id]);

    return (
        <>
            <Head>
                {subBranchTitle(branch, "Promotion levels")}
            </Head>
            <Skeleton loading={loading} active>
                <MainPage
                    title="Promotion levels"
                    breadcrumbs={downToBranchBreadcrumbs({branch})}
                    commands={commands}
                >
                    <List
                        itemLayout="horizontal"
                        dataSource={branch.promotionLevels}
                        renderItem={(pl) => (
                            <List.Item>
                                <List.Item.Meta
                                    title={<PromotionLevelLink promotionLevel={pl}/>}
                                    description={pl.description}
                                />
                            </List.Item>
                        )}
                    />
                </MainPage>
            </Skeleton>
        </>
    )
}