"use client"

/**
 * One branch, on a phone: its latest builds.
 *
 * This is the screen that most needs to diverge from the desktop UI, and the
 * only one that does more than restyle it. `BranchBuilds` is a matrix with a
 * column per validation stamp - wide by construction - so a phone gets a card
 * per build instead. See `MobileBuildCard` for what a card carries and what it
 * deliberately leaves to the build screen.
 *
 * Everything else the desktop branch page offers - build filters, the validation
 * stamp filters, branch links, the delivery map, the change log - is out of
 * scope here and reached through the interstitial.
 */

import {useState} from "react"
import {gql} from "graphql-request"
import Link from "next/link"
import {Button, Empty, Spin, Tag} from "antd"
import {useQuery} from "@components/services/GraphQL"
import MobileScreen from "@components/mobile/layout/MobileScreen"
import MobileAsyncContent from "@components/mobile/layout/MobileAsyncContent"
import MobileFavourite from "@components/mobile/favourites/MobileFavourite"
import {useFavouriteRefresh} from "@components/mobile/favourites/useFavouriteRefresh"
import MobileBuildCard from "@components/mobile/builds/MobileBuildCard"
import {mobileProjectUri} from "@components/mobile/mobileRoutes"

/**
 * How many builds one page holds.
 *
 * A phone screen shows three or four cards at a time, so a page is a few
 * flicks' worth: enough that scrolling is not immediately interrupted, small
 * enough that the first answer arrives quickly on a phone network.
 */
export const MOBILE_BUILD_PAGE_SIZE = 10

export default function MobileBranchScreen({id}) {

    /*
     * "Load more" grows the page rather than accumulating pages in the browser.
     * Merging pages by hand means owning a second copy of the list and keeping
     * it in step with the favourite toggle's refetches; refetching a longer
     * first page cannot drift, and at these sizes it costs one query against an
     * index the branch page already hits.
     */
    const [size, setSize] = useState(MOBILE_BUILD_PAGE_SIZE)

    const {refresh, onToggled} = useFavouriteRefresh()

    const query = useQuery(
        gql`
            query MobileBranch($id: Int!, $size: Int!) {
                branch(id: $id) {
                    id
                    name
                    displayName
                    disabled
                    favourite
                    project {
                        id
                        name
                    }
                    buildsPaginated(offset: 0, size: $size) {
                        pageInfo {
                            nextPage {
                                offset
                            }
                        }
                        pageItems {
                            id
                            name
                            # Already the release property when there is one, and
                            # the build's own name otherwise - a build name is a
                            # timestamp-run pair, not a version.
                            displayName
                            creation {
                                time
                            }
                            # The last run per level: a build promoted twice to
                            # the same level is still at that level once.
                            promotionRuns(lastPerLevel: true) {
                                id
                                promotionLevel {
                                    id
                                    name
                                    image
                                }
                            }
                            # Where the build is *now*, which is the question a
                            # phone user has. The full pipeline history is a
                            # desktop surface.
                            currentDeployments {
                                id
                                slot {
                                    id
                                    environment {
                                        id
                                        name
                                    }
                                }
                            }
                        }
                    }
                }
            }
        `,
        {
            variables: {id: Number(id), size},
            deps: [id, size, refresh],
        }
    )

    const branch = query.data?.branch
    const builds = branch?.buildsPaginated?.pageItems ?? []
    const hasMore = Boolean(branch?.buildsPaginated?.pageInfo?.nextPage)

    const branchName = branch ? (branch.displayName || branch.name) : "Branch"

    return (
        <MobileScreen
            title={branchName}
            subtitle={
                branch?.project &&
                <Link href={mobileProjectUri(branch.project.id)}>
                    {branch.project.name}
                </Link>
            }
            extra={
                branch &&
                <MobileFavourite
                    type="branch"
                    id={branch.id}
                    name={branchName}
                    favourite={branch.favourite}
                    onToggled={onToggled}
                />
            }
        >
            {
                branch?.disabled &&
                <Tag color="default" data-testid="mobile-branch-disabled">Disabled</Tag>
            }
            <MobileAsyncContent
                state={query}
                /*
                 * Also what a missing branch looks like: the root `branch(id:)`
                 * field is non-null, so an id nobody can see comes back as a
                 * GraphQL error rather than as a null - unlike the project
                 * screen, which has a nullable field and its own "no such
                 * project" state.
                 */
                errorMessage="Could not load the branch."
                isEmpty={builds.length === 0}
                rows={6}
                empty={
                    <div data-testid="mobile-builds-empty">
                        <Empty
                            image={Empty.PRESENTED_IMAGE_SIMPLE}
                            description="This branch has no build yet."
                        />
                    </div>
                }
            >
                <ul className="ot-mobile-cards" data-testid="mobile-builds">
                    {
                        builds.map(build => <MobileBuildCard key={build.id} build={build}/>)
                    }
                </ul>
                {
                    hasMore &&
                    <div className="ot-mobile-note">
                        <Button
                            block
                            data-testid="mobile-builds-more"
                            disabled={query.loading}
                            onClick={() => setSize(current => current + MOBILE_BUILD_PAGE_SIZE)}
                        >
                            {query.loading ? <Spin size="small"/> : "Load more"}
                        </Button>
                    </div>
                }
            </MobileAsyncContent>
        </MobileScreen>
    )
}
