import {Descriptions, Typography} from "antd";
import Link from "next/link";

export default function JenkinsNotificationChannelOutput({jobUrl, parameters = []}) {
    return (
        <>
            <Descriptions
                column={12}
                items={[
                    {
                        key: 'jobUrl',
                        label: 'Job',
                        children: <Link href={jobUrl}>{jobUrl}</Link>,
                        span: 12,
                    },
                    {
                        key: 'parameters',
                        label: 'Parameters',
                        children: <>
                            <Descriptions
                                items={parameters.map(({name, value}) => (
                                    {
                                        key: name,
                                        label: name,
                                        children: <code>{value}</code>,
                                        span: 12,
                                    }
                                ))}
                            />
                            {
                                parameters.length === 0 && <Typography.Text type="secondary">None</Typography.Text>
                            }
                        </>,
                        span: 12,
                    }
                ]}
            />
        </>
    )
}