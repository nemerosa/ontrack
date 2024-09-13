import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import Link from "next/link";
import {CloseCommand} from "@components/common/Commands";
import NotificationRecordDetails from "@components/extension/notifications/NotificationRecordDetails";
import {useEffect, useState} from "react";
import LoadingContainer from "@components/common/LoadingContainer";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {gqlNotificationRecordContent} from "@components/extension/notifications/NotificationRecordsGraphQLFragments";

export default function NotificationRecordingView({id}) {

    const client = useGraphQLClient()

    const [record, setRecord] = useState({})
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query NotificationRecord($id: String!) {
                        notificationRecord(id: $id) {
                            ...NotificationRecordContent
                        }
                    }

                    ${gqlNotificationRecordContent}
                `,
                {id}
            ).then(data => {
                setRecord(data.notificationRecord)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id])

    return (
        <>
            <Head>
                {pageTitle(`Notification recording ${id}`)}
            </Head>
            <MainPage
                title={id}
                breadcrumbs={[
                    ...homeBreadcrumbs(),
                    <Link
                        key="notifications"
                        href={'/extension/notifications/recordings'}
                    >
                        Notification recordings
                    </Link>,
                ]}
                commands={[
                    <CloseCommand key="close" href={'/extension/notifications/recordings'}/>
                ]}
            >
                <LoadingContainer loading={loading}>
                    <NotificationRecordDetails record={record} includeAll={true}/>
                </LoadingContainer>
            </MainPage>
        </>
    )
}