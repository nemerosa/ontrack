import {useRouter} from "next/router";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {gqlProjectCommonFragment, gqlPropertiesFragment} from "@components/services/fragments";
import StandardPage from "@components/layouts/StandardPage";
import AutoVersioningAuditView from "@components/extension/auto-versioning/AutoVersioningAuditView";

export default function AutoVersioningAuditProjectTargetPage() {
    const router = useRouter()
    const {id} = router.query

    console.log({id})

    const client = useGraphQLClient()

    const [project, setProject] = useState()
    useEffect(() => {
        if (client && id) {
            client.request(
                gql`
                    query GetProject(
                        $id: Int!,
                    ) {
                        projects(id: $id) {
                            ...projectCommonFragment
                        }
                    }

                    ${gqlProjectCommonFragment}
                `,
                {id}
            ).then(data => {
                setProject(data.projects[0])
            })
        }
    }, [client, id]);

    return (
        <>
            <StandardPage
                pageTitle={`Auto-versioning audit for source project ${project?.name}`}
            >
                <AutoVersioningAuditView sourceProject={project}/>
            </StandardPage>
        </>
    )
}