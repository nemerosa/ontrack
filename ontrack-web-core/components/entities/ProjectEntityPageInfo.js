import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useEffect, useState} from "react";
import {promotionLevelTitleName} from "@components/common/Titles";
import {promotionLevelBreadcrumbs} from "@components/common/Breadcrumbs";
import PromotionLevelViewTitle from "@components/promotionLevels/PromotionLevelViewTitle";
import {promotionLevelUri} from "@components/common/Links";

export const useProjectEntityPageInfo = (type, id) => {
    const client = useGraphQLClient()
    const [title, setTitle] = useState('')
    const [breadcrumbs, setBreadcrumbs] = useState([])
    const [closeUri, setCloseUri] = useState('')
    const [entity, setEntity] = useState({})

    useEffect(() => {
        if (client && type && id) {
            switch (type) {
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