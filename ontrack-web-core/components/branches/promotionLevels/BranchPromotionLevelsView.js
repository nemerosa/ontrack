import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import Head from "next/head";
import {subBranchTitle} from "@components/common/Titles";
import {downToBranchBreadcrumbs} from "@components/common/Breadcrumbs";
import MainPage from "@components/layouts/MainPage";
import {List, Skeleton, Space} from "antd";
import {gql} from "graphql-request";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";
import {gqlDecorationFragment} from "@components/services/fragments";
import Decorations from "@components/framework/decorations/Decorations";
import {isAuthorized} from "@components/common/authorizations";
import PromotionLevelCreateCommand from "@components/promotionLevels/PromotionLevelCreateCommand";

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
                            authorizations {
                                name
                                action
                                authorized
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
                                decorations {
                                    ...decorationContent
                                }
                            }
                        }
                    }

                    ${gqlDecorationFragment}
                `,
                {id}
            ).then(data => {
                setBranch(data.branch)

                const commands = []
                if (isAuthorized(data.branch, 'promotion_level', 'create')) {
                    commands.push(
                        <PromotionLevelCreateCommand key="create"/>
                    )
                }
                commands.push(
                    <CloseCommand key="close" href={branchUri(data.branch)}/>
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
                                    title={
                                        <Space>
                                            <PromotionLevelLink promotionLevel={pl}/>
                                            <Decorations entity={pl}/>
                                        </Space>
                                    }
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