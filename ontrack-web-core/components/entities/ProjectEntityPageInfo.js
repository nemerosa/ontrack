import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useEffect, useState} from "react";
import {projectTitleName, promotionLevelTitleName} from "@components/common/Titles";
import {projectBreadcrumbs, promotionLevelBreadcrumbs} from "@components/common/Breadcrumbs";
import PromotionLevelViewTitle from "@components/promotionLevels/PromotionLevelViewTitle";
import {projectUri, promotionLevelUri} from "@components/common/Links";
import ProjectLink from "@components/projects/ProjectLink";

export const useProjectEntityPageInfo = (type, id) => {
    const client = useGraphQLClient()
    const [title, setTitle] = useState('')
    const [breadcrumbs, setBreadcrumbs] = useState([])
    const [closeUri, setCloseUri] = useState('')
    const [entity, setEntity] = useState({})

    useEffect(() => {
        if (client && type && id) {
            switch (type) {
                case 'PROJECT': {
                    client.request(
                        gql`
                            query EntityInformation( $id: Int!, ) {
                                project(id: $id) {
                                    id
                                    name
                                    authorizations {
                                        name
                                        action
                                        authorized
                                    }
                                }
                            }
                        `, {id}
                    ).then(data => {
                        setTitle(projectTitleName(data.project, 'Subscriptions'))

                        const breadcrumbs = projectBreadcrumbs()
                        breadcrumbs.push(
                            <ProjectLink project={data.project}/>
                        )
                        setBreadcrumbs(breadcrumbs)

                        setCloseUri(projectUri(data.project))

                        setEntity(data.project)
                    })
                    break
                }
                case 'PROMOTION_LEVEL': {
                    client.request(
                        gql`
                            query EntityInformation( $id: Int!, ) {
                                promotionLevel(id: $id) {
                                    id
                                    name
                                    image
                                    branch {
                                        id
                                        name
                                        displayName
                                        project {
                                            id
                                            name
                                        }
                                    }
                                    authorizations {
                                        name
                                        action
                                        authorized
                                    }
                                }
                            }
                        `, {id}
                    ).then(data => {
                        setTitle(promotionLevelTitleName(data.promotionLevel, 'Subscriptions'))

                        const breadcrumbs = promotionLevelBreadcrumbs(data.promotionLevel)
                        breadcrumbs.push(
                            <PromotionLevelViewTitle
                                key="entity"
                                promotionLevel={data.promotionLevel}
                                link={true}
                            />
                        )
                        setBreadcrumbs(breadcrumbs)

                        setCloseUri(promotionLevelUri(data.promotionLevel))

                        setEntity(data.promotionLevel)
                    })
                    break
                }
            }
        }
    }, [client, type, id]);

    return {
        title,
        breadcrumbs,
        closeUri,
        entity,
    }
}