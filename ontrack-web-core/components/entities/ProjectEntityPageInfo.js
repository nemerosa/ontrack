import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useEffect, useState} from "react";
import {
    branchTitleName,
    projectTitleName,
    promotionLevelTitleName,
    validationStampTitleName
} from "@components/common/Titles";
import {
    downToBranchBreadcrumbs,
    projectBreadcrumbs,
    promotionLevelBreadcrumbs,
    validationStampBreadcrumbs
} from "@components/common/Breadcrumbs";
import PromotionLevelViewTitle from "@components/promotionLevels/PromotionLevelViewTitle";
import {branchUri, projectUri, promotionLevelUri, validationStampUri} from "@components/common/Links";
import ProjectLink from "@components/projects/ProjectLink";
import ValidationStampViewTitle from "@components/validationStamps/ValidationStampViewTitle";

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
                case 'BRANCH': {
                    client.request(
                        gql`
                            query EntityInformation( $id: Int!, ) {
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
                                }
                            }
                        `, {id}
                    ).then(data => {
                        setTitle(branchTitleName(data.branch, 'Subscriptions'))
                        setBreadcrumbs(downToBranchBreadcrumbs(data))
                        setCloseUri(branchUri(data.branch))
                        setEntity(data.branch)
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
                case 'VALIDATION_STAMP': {
                    client.request(
                        gql`
                            query EntityInformation( $id: Int!, ) {
                                validationStamp(id: $id) {
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
                        setTitle(validationStampTitleName(data.validationStamp, 'Subscriptions'))

                        const breadcrumbs = validationStampBreadcrumbs(data.validationStamp)
                        breadcrumbs.push(
                            <ValidationStampViewTitle
                                key="entity"
                                validationStamp={data.validationStamp}
                                link={true}
                            />
                        )
                        setBreadcrumbs(breadcrumbs)

                        setCloseUri(validationStampUri(data.validationStamp))

                        setEntity(data.validationStamp)
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