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
 * reach a project the server had not already sent.
 *
 * The project screen behind each row is #1721; until it exists a row is a name
 * and its star, which is all this screen has to do for the home screen to work.
 */

import {useEffect, useMemo, useState} from "react"
import {gql} from "graphql-request"
import debounce from "lodash.debounce"
import {Empty, Input, Tag} from "antd"
import {useQuery} from "@components/services/GraphQL"
import MobileScreen from "@components/mobile/layout/MobileScreen"
import MobileAsyncContent from "@components/mobile/layout/MobileAsyncContent"
import {MobileEntityGroup, MobileEntityRow} from "@components/mobile/entities/MobileEntityList"
import MobileFavourite from "@components/mobile/favourites/MobileFavourite"

/**
 * How long the typing has to settle before the list is fetched again. Long
 * enough that a word is one request rather than one per letter, short enough
 * that it does not read as lag.
 */
const FILTER_DEBOUNCE_MS = 400

export default function MobileProjectListScreen() {

    // What is in the box, and what has been asked for - two values, because the
    // second lags the first by the debounce and the input must not.
    const [typed, setTyped] = useState('')
    const [pattern, setPattern] = useState('')

    // Refetched rather than patched in place, for the reason the home screen
    // gives: one source of truth for whether something is a favourite.
    const [refresh, setRefresh] = useState(0)

    const applyPattern = useMemo(
        () => debounce((value) => setPattern(value), FILTER_DEBOUNCE_MS),
        []
    )

    // A timer must not outlive the screen that armed it - leaving the tab within
    // the debounce would otherwise fire a state update into an unmounted tree.
    useEffect(() => () => applyPattern.cancel(), [applyPattern])

    const onFilterChange = (event) => {
        const value = event.target.value
        setTyped(value)
        if (value) {
            applyPattern(value)
        } else {
            // Clearing is immediate: the user asking for the whole list back
            // should not wait on a debounce that is only there to spare the
            // server a request per keystroke.
            applyPattern.cancel()
            setPattern('')
        }
    }

    /*
     * Trimmed, because the server tests the pattern with `isNullOrBlank` and
     * answers a blank one with the *whole* list. A screen that called itself
     * filtered on a lone space would head every project on the instance with
     * "Matching projects".
     */
    const filter = pattern.trim()
    const filtering = filter.length > 0

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
            variables: {pattern: filtering ? filter : null},
            deps: [pattern, refresh],
        }
    )

    const projects = query.data?.projects ?? []

    return (
        <MobileScreen title="Projects">
            <Input
                allowClear
                value={typed}
                onChange={onFilterChange}
                placeholder="Filter by name"
                aria-label="Filter the projects by name"
                data-testid="mobile-projects-filter"
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
                                filtering
                                    ? `No project matches "${filter}".`
                                    : "There is no project on this instance yet."
                            }
                        />
                    </div>
                }
            >
                <MobileEntityGroup
                    title={filtering ? "Matching projects" : "All projects"}
                    testId="mobile-projects"
                >
                    {
                        projects.map(project =>
                            <MobileEntityRow
                                key={project.id}
                                testId={`mobile-project-${project.id}`}
                                name={project.name}
                                context={
                                    project.disabled ? <Tag color="default">Disabled</Tag> : undefined
                                }
                                action={
                                    <MobileFavourite
                                        type="project"
                                        id={project.id}
                                        name={project.name}
                                        favourite={project.favourite}
                                        onToggled={() => setRefresh(count => count + 1)}
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
