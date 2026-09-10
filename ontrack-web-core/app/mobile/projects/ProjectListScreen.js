"use client"

/**
 * The project list: every project the user can see, one tap from the home screen.
 *
 * It is home's counterpart. Home is the short, curated list; this is the whole
 * one, and the only place a user with no favourites yet can make some - which is
 * why it carries the favourite toggle and is what the empty state points at.
 *
 * An instance can hold hundreds of projects, which is more than anyone scrolls
 * through on a phone, so the list is filterable by name. The filter runs on the
 * server (`projects(pattern:)`, an `ILIKE '%...%'` ordered by name): a
 * client-side one could only narrow the answer to the last query and would never
 * reach a project the server had not already sent. The typing itself is handled
 * by `useMobileFilter`, shared with the project screen's branch filter.
 *
 * Each row goes to that project's mobile screen, which is where its branches are.
 */

import {gql} from "graphql-request"
import {Empty, Tag} from "antd"
import {useQuery} from "@components/services/GraphQL"
import MobileScreen from "@components/mobile/layout/MobileScreen"
import MobileAsyncContent from "@components/mobile/layout/MobileAsyncContent"
import {MobileEntityGroup, MobileEntityRow} from "@components/mobile/entities/MobileEntityList"
import {MobileFilterInput, useMobileFilter} from "@components/mobile/entities/MobileFilter"
import MobileFavourite from "@components/mobile/favourites/MobileFavourite"
import {useFavouriteRefresh} from "@components/mobile/favourites/useFavouriteRefresh"
import {mobileProjectUri} from "@components/mobile/mobileRoutes"

export default function MobileProjectListScreen() {

    const filter = useMobileFilter()

    const {refresh, onToggled} = useFavouriteRefresh()

    const query = useQuery(
        gql`
            query MobileProjects($pattern: String) {
                projects(pattern: $pattern) {
                    id
                    name
                    disabled
                    favourite
                }
            }
        `,
        {
            /*
             * `null` and not `''` when nothing is typed: the server refuses
             * `pattern` alongside any other argument, and it tells the two apart
             * by whether the argument was *supplied* - a null one is not.
             */
            variables: {pattern: filter.filtering ? filter.filter : null},
            deps: [filter.filter, refresh],
        }
    )

    const projects = query.data?.projects ?? []

    return (
        <MobileScreen title="Projects">
            <MobileFilterInput
                filter={filter}
                label="Filter the projects by name"
                testId="mobile-projects-filter"
            />
            <MobileAsyncContent
                state={query}
                errorMessage="Could not load the projects."
                isEmpty={projects.length === 0}
                rows={6}
                empty={
                    <div data-testid="mobile-projects-empty">
                        <Empty
                            image={Empty.PRESENTED_IMAGE_SIMPLE}
                            description={
                                // Two different facts, and telling them apart is
                                // the difference between "type something else"
                                // and "there is nothing here to find".
                                filter.filtering
                                    ? `No project matches "${filter.filter}".`
                                    : "There is no project on this instance yet."
                            }
                        />
                    </div>
                }
            >
                <MobileEntityGroup
                    title={filter.filtering ? "Matching projects" : "All projects"}
                    testId="mobile-projects"
                >
                    {
                        projects.map(project =>
                            <MobileEntityRow
                                key={project.id}
                                testId={`mobile-project-${project.id}`}
                                name={project.name}
                                href={mobileProjectUri(project.id)}
                                context={
                                    project.disabled ? <Tag color="default">Disabled</Tag> : undefined
                                }
                                action={
                                    <MobileFavourite
                                        type="project"
                                        id={project.id}
                                        name={project.name}
                                        favourite={project.favourite}
                                        onToggled={onToggled}
                                    />
                                }
                            />
                        )
                    }
                </MobileEntityGroup>
            </MobileAsyncContent>
        </MobileScreen>
    )
}
