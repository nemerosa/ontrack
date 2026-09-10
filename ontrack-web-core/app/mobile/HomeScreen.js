"use client"

/**
 * The mobile home screen: the user's favourites.
 *
 * Someone reaching for their phone is checking something they already care
 * about, so home is their favourites rather than a full project list - which is
 * one tap away in the bottom bar for the times it is not.
 *
 * No new GraphQL: `projects(favourites: true)` and `branches(favourite: true)`
 * are what the desktop dashboard widgets already read.
 */

import {useState} from "react"
import {gql} from "graphql-request"
import {useQuery} from "@components/services/GraphQL"
import MobileScreen from "@components/mobile/layout/MobileScreen"
import MobileAsyncContent from "@components/mobile/layout/MobileAsyncContent"
import {MobileEntityGroup, MobileEntityRow} from "@components/mobile/entities/MobileEntityList"
import MobileFavourite from "@components/mobile/favourites/MobileFavourite"
import MobileFavouritesEmpty from "@components/mobile/favourites/MobileFavouritesEmpty"
import {mobileBranchUri, mobileProjectUri} from "@components/mobile/mobileRoutes"

export default function MobileHomeScreen() {

    /*
     * Unstarring from this screen has to take the row away, and the only honest
     * way to know it did is to ask the server again. A local copy of the two
     * lists would be a second source of truth for the same fact, and the lists
     * are short enough that refetching them costs nothing.
     *
     * The mobile provider stack has no `EventsContextProvider` - the desktop
     * favourite widgets refresh off a page event - so the counter is local.
     */
    const [refresh, setRefresh] = useState(0)
    const onToggled = () => setRefresh(count => count + 1)

    const query = useQuery(
        gql`
            query MobileFavourites {
                projects(favourites: true) {
                    id
                    name
                    favourite
                }
                branches(favourite: true) {
                    id
                    name
                    displayName
                    favourite
                    project {
                        id
                        name
                    }
                }
            }
        `,
        {deps: [refresh]}
    )

    const projects = query.data?.projects ?? []
    const branches = query.data?.branches ?? []

    return (
        <MobileScreen title="Home">
            <MobileAsyncContent
                state={query}
                errorMessage="Could not load your favourites."
                isEmpty={projects.length === 0 && branches.length === 0}
                empty={<MobileFavouritesEmpty/>}
                rows={4}
            >
                {
                    projects.length > 0 &&
                    <MobileEntityGroup title="Favourite projects" testId="mobile-favourite-projects">
                        {
                            projects.map(project =>
                                <MobileEntityRow
                                    key={project.id}
                                    testId={`mobile-project-${project.id}`}
                                    name={project.name}
                                    href={mobileProjectUri(project.id)}
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
                }
                {
                    branches.length > 0 &&
                    <MobileEntityGroup title="Favourite branches" testId="mobile-favourite-branches">
                        {
                            branches.map(branch =>
                                <MobileEntityRow
                                    key={branch.id}
                                    testId={`mobile-branch-${branch.id}`}
                                    name={branch.displayName || branch.name}
                                    href={mobileBranchUri(branch.id)}
                                    // Favourite branches come from every project
                                    // at once, so a bare branch name says too
                                    // little - two projects can both have a `main`.
                                    context={branch.project?.name}
                                    action={
                                        <MobileFavourite
                                            type="branch"
                                            id={branch.id}
                                            name={branch.displayName || branch.name}
                                            favourite={branch.favourite}
                                            onToggled={onToggled}
                                        />
                                    }
                                />
                            )
                        }
                    </MobileEntityGroup>
                }
            </MobileAsyncContent>
        </MobileScreen>
    )
}
