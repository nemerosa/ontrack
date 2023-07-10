import {Breadcrumb, Typography} from "antd";
import MainPageCommands from "@components/layouts/MainPageCommands";
import {useEffect, useState} from "react";

const {Text} = Typography;

export default function MainPageBar({breadcrumbs, title, commands}) {

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
        </>
    )
}