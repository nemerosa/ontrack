"use client"

/**
 * One project, on a phone: its branches.
 *
 * The desktop project page carries branch boxes with their last promotions,
 * decorations, an info drawer and a row of commands. None of that is what
 * someone opens on a phone for - they are on their way to a branch, and from
 * there to a build. So this screen is the mobile list shape and nothing else: a
 * branch per row, its star, and a tap through to it.
 *
 * The branch list is **limited and filterable**, for the same reason the project
 * list is: a project can hold hundreds of branches, and a phone shows a handful
 * of rows. The limit keeps the screen honest about that rather than pretending
 * to be a full list, and the filter is how a user reaches the branch the limit
 * left out. Both run on the server - see `MobileFilter` and `branchNamePattern`.
 */

import {useState} from "react"
import {gql} from "graphql-request"
import {Alert, Empty, Tag, Typography} from "antd"
import {useQuery} from "@components/services/GraphQL"
import MobileScreen from "@components/mobile/layout/MobileScreen"
import MobileAsyncContent from "@components/mobile/layout/MobileAsyncContent"
import {MobileEntityGroup, MobileEntityRow} from "@components/mobile/entities/MobileEntityList"
import {MobileFilterInput, useMobileFilter} from "@components/mobile/entities/MobileFilter"
import {branchNamePattern} from "@components/mobile/entities/branchNamePattern"
import MobileFavourite from "@components/mobile/favourites/MobileFavourite"
import {mobileBranchUri} from "@components/mobile/mobileRoutes"

/**
 * How many branches the screen shows at once.
 *
 * Enough to cover a project's living branches - the list is ordered by build
 * activity, so those come first - without turning the screen into something to
 * be scrolled rather than read. Beyond it, the filter is the way through.
 */
export const MOBILE_BRANCH_LIMIT = 20

export default function MobileProjectScreen({id}) {

    const filter = useMobileFilter()

    // Refetched rather than patched in place, for the reason the home screen
    // gives: one source of truth for whether something is a favourite.
    const [refresh, setRefresh] = useState(0)

    const query = useQuery(
        gql`
            query MobileProject($id: Int!, $name: String, $count: Int!) {
                project(id: $id) {
                    id
                    name
                    favourite
                    # Ordered by the most recent build activity, so what the
                    # limit below keeps is what someone is most likely here for.
                    branches(name: $name, count: $count, order: true) {
                        id
                        name
                        displayName
                        disabled
                        favourite
                    }
                }
            }
        `,
        {
            variables: {
                // `null` and not `''` - the server treats a blank name as no
                // filter at all, so sending one would be a lie the screen then
                // has to tell in its heading too.
                name: branchNamePattern(filter.filter),
                /*
                 * One more than is shown. `branches` answers with a plain list
                 * and no total, so the extra row is the only way to know whether
                 * anything was left out - and it costs exactly one row.
                 */
                count: MOBILE_BRANCH_LIMIT + 1,
                id: Number(id),
            },
            deps: [id, filter.filter, refresh],
        }
    )

    const project = query.data?.project
    // A project that is not there is a different answer from a project with no
    // branch, and the screen must not tell the second story for the first.
    const missing = query.finished && !query.loading && !project

    const found = project?.branches ?? []
    const branches = found.slice(0, MOBILE_BRANCH_LIMIT)
    const truncated = found.length > MOBILE_BRANCH_LIMIT

    return (
        <MobileScreen
            title={project?.name ?? "Project"}
            extra={
                project &&
                <MobileFavourite
                    type="project"
                    id={project.id}
                    name={project.name}
                    favourite={project.favourite}
                    onToggled={() => setRefresh(count => count + 1)}
                />
            }
        >
            {
                !missing &&
                <MobileFilterInput
                    filter={filter}
                    label="Filter the branches by name"
                    testId="mobile-branches-filter"
                />
            }
            <MobileAsyncContent
                state={query}
                errorMessage="Could not load the project."
                isEmpty={missing || branches.length === 0}
                rows={6}
                empty={
                    missing ?
                        <Alert
                            data-testid="mobile-project-not-found"
                            type="warning"
                            showIcon
                            message="No such project"
                            description="It may have been deleted, or you may not be allowed to see it."
                        /> :
                        <div data-testid="mobile-branches-empty">
                            <Empty
                                image={Empty.PRESENTED_IMAGE_SIMPLE}
                                description={
                                    filter.filtering
                                        ? `No branch matches "${filter.filter}".`
                                        : "This project has no branch yet."
                                }
                            />
                        </div>
                }
            >
                <MobileEntityGroup
                    title={filter.filtering ? "Matching branches" : "Branches"}
                    testId="mobile-branches"
                >
                    {
                        branches.map(branch =>
                            <MobileEntityRow
                                key={branch.id}
                                testId={`mobile-branch-${branch.id}`}
                                name={branch.displayName || branch.name}
                                href={mobileBranchUri(branch.id)}
                                context={
                                    branch.disabled ? <Tag color="default">Disabled</Tag> : undefined
                                }
                                action={
                                    <MobileFavourite
                                        type="branch"
                                        id={branch.id}
                                        name={branch.displayName || branch.name}
                                        favourite={branch.favourite}
                                        onToggled={() => setRefresh(count => count + 1)}
                                    />
                                }
                            />
                        )
                    }
                </MobileEntityGroup>
                {
                    truncated &&
                    <Typography.Paragraph
                        type="secondary"
                        className="ot-mobile-note"
                        data-testid="mobile-branches-truncated"
                    >
                        {`Showing the ${MOBILE_BRANCH_LIMIT} most recently active branches. Filter by name to find another.`}
                    </Typography.Paragraph>
                }
            </MobileAsyncContent>
        </MobileScreen>
    )
}
