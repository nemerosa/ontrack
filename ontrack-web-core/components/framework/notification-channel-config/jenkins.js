import {Space, Tag, Tooltip, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import Link from "next/link";
import {FaArrowRight} from "react-icons/fa";
import Duration from "@components/common/Duration";

export default function JenkinsNotificationChannelConfig({config, job, parameters, callMode, timeout}) {

    const client = useGraphQLClient()
    const [url, setUrl] = useState('')

    useEffect(() => {
        if (client && config) {
            client.request(
                gql`
                    query GetJenkinsConfiguration($config: String!) {
                        jenkinsConfiguration(name: $config) {
                            url
                        }
                    }
                `,
                {config}
            ).then(data => {
                setUrl(data.jenkinsConfiguration?.url)
            })
        }
    }, [client, config]);

    return (
        <>
            <p>
                <Space size={4}>
                    <Typography.Text>Triggering job at</Typography.Text>
                    {
                        url && <Link href={url}>{url}</Link>
                    }
                    (<Typography.Text code>{config}</Typography.Text>)
                    <Typography.Text code>{job}</Typography.Text>
                </Space>
            </p>
            {
                parameters && parameters.length > 0 &&
                <>
                    <p>
                        Parameters:
                    </p>
                    <ul>
                        {
                            parameters.map(({name, value}) => (
                                <li key={name}>
                                    <Space>
                                        <Typography.Text code>{name}</Typography.Text>
                                        <FaArrowRight/>
                                        <Typography.Text code>{value}</Typography.Text>
                                    </Space>
                                </li>
                            ))
                        }
                    </ul>
                </>
            }
            <p>
                <Space>
                Call mode:
                {
                    callMode === 'ASYNC' && 'Asynchronous (fire and forget)'
                }
                {
                    callMode === 'SYNC' && 'Synchronous (waits for completion)'
                }
                </Space>
            </p>
            <p>
                <Space>
                    <Typography.Text>Timeout:</Typography.Text>
                    <Duration seconds={timeout} displaySecondsInTooltip={true}/>
                </Space>
            </p>
        </>
    )
}