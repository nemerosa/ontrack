import {Button, Space, Tag} from "antd";
import {useContext, useEffect, useState} from "react";
import {DashboardContext} from "@components/dashboards/DashboardContextProvider";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {FaCheck, FaRegCopy} from "react-icons/fa";
import copy from "copy-to-clipboard";
import {useRouter} from "next/router";
import {useConnection} from "@components/providers/ConnectionContextProvider";

export default function DashboardPageTitle({title}) {

    const router = useRouter()
    const {environment} = useConnection()
    const {dashboard} = useContext(DashboardContext)

    const [copied, setCopied] = useState(false)

    useEffect(() => {
        setCopied(false)
    }, [dashboard?.uuid])

    const copyDashboardLink = () => {
        const link = `${environment.ontrack.ui.url}${router.pathname}?dashboard=${dashboard.uuid}`
        setCopied(copy(link))
    }

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
                <FaRegCopy
                    className="ot-action"
                    title="Copy a link to this dashboard."
                    onClick={copyDashboardLink}
                />
                {
                    copied &&
                    <Button
                        type="text"
                        icon={<FaCheck/>}
                        disabled={true}
                    >
                        Copied
                    </Button>
                }
            </Space>
        </>
    )
}