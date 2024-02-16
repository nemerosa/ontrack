import {Space, Tag} from "antd";
import {useContext} from "react";
import {DashboardContext} from "@components/dashboards/DashboardContextProvider";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";

export default function DashboardPageTitle({title}) {

    const {dashboard} = useContext(DashboardContext)

    return (
        <>
            <Head>
                {pageTitle(`${title} - ${dashboard?.name}`)}
            </Head>
            <Space>
                {title}
                <Tag>
                    {dashboard?.name}
                </Tag>
            </Space>
        </>
    )
}