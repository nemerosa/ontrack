import {Breadcrumb, Space, Typography} from "antd";
import MainPageCommands from "@components/layouts/MainPageCommands";
import {useEffect, useState} from "react";

const {Text} = Typography;

export default function MainPageBar({breadcrumbs, title, commands, description}) {

    const [actualBreadcrumbs, setActualBreadcrumbs] = useState([])
    useEffect(() => {
        const list = breadcrumbs.map(b => ({
            title: b,
        }))
        list.push({
            title: <Text strong>{title}</Text>
        })
        setActualBreadcrumbs(list)
    }, [breadcrumbs, title])

    return (
        <>
            <Space direction="vertical" className="ot-line" size={0}>
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                }}>
                    <Breadcrumb
                        style={{
                            margin: '16px 0',
                        }}
                        items={actualBreadcrumbs}
                    />

                    {/* Commands on the right */}
                    <MainPageCommands commands={commands}/>
                </div>
                {
                    description && <div
                        style={{
                            marginBottom: 16,
                        }}
                    >
                        <Typography.Text disabled>{description}</Typography.Text>
                    </div>
                }
            </Space>
        </>
    )
}