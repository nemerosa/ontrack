import {Space, Typography} from "antd";
import Link from "next/link";
import {callDynamicFunction} from "@components/common/DynamicFunction";
import {useEffect, useState} from "react";

export default function SearchResult({type, result}) {

    const [typedResult, setTypedResult] = useState({
        title: '',
        link: '',
        description: ''
    })

    useEffect(() => {

        const loadTypedResult = async () => {
            const data = await callDynamicFunction(`framework/search/${type.id}/Result`, result);
            setTypedResult(data)
        }

        // noinspection JSIgnoredPromiseFromCall
        loadTypedResult()
    }, [type, result])


    return (
        <>
            {
                typedResult &&
                <Space direction="vertical">
                    {
                        typedResult.link &&
                        <Link href={typedResult.link}>{typedResult.title}</Link>
                    }
                    {
                        !typedResult.link && typedResult.title
                    }
                    <Typography.Paragraph type="secondary">{typedResult.description}</Typography.Paragraph>
                </Space>
            }
        </>
    )
}