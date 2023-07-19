import {gql} from "graphql-request";
import {gqlDecorationFragment} from "@components/services/fragments";

export const gqlBuilds = gql`
    query LoadBuilds(
        $branchId: Int!,
        $offset: Int!,
        $size: Int!,
        $filterType: String,
        $filterData: String,
    ) {
        branches(id: $branchId) {
            buildsPaginated(
                offset: $offset,
                size: $size,
                generic: {
                    type: $filterType,
                    data: $filterData
                }
            ) {
                pageInfo {
                    totalSize
                    nextPage {
                        offset
                        size
                    }
                }
                pageItems {
                    id
                    name
                    creation {
                        time
                    }
                    decorations {
                        ...decorationContent
                    }
                    promotionRuns(lastPerLevel: true) {
                        id
                        creation {
                            time
                        }
                        promotionLevel {
                            id
                            name
                            description
                            image
                            _image
                        }
                    }
                    validations {
                        validationStamp {
                            id
                            name
                        }
                        validationRuns(count: 1) {
                            validationRunStatuses(lastOnly: true) {
                                statusID {
                                    id
                                    name
                                }
                            }
                        }
                    }
                    links {
                        _validate
                    }
                }
            }
        }
    }
    
    ${gqlDecorationFragment}
`