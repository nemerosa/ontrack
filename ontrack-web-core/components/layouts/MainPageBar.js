import {Breadcrumb, Typography} from "antd";
import MainPageCommands from "@components/layouts/MainPageCommands";

const {Text} = Typography;

export default function MainPageBar({breadcrumbs, title, commands}) {
    return (
        <>
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
            }}>
                {/* Breadcrumbs on the left */}
                <Breadcrumb
                    style={{
                        margin: '16px 0',
                    }}
                >
                    {
                        breadcrumbs ?
                            breadcrumbs.map((item, index) => {
                                return <Breadcrumb.Item key={index}>{item}</Breadcrumb.Item>
                            }) :
                            undefined
                    }
                    <Breadcrumb.Item><Text strong>{title}</Text></Breadcrumb.Item>
                </Breadcrumb>

                {/* Commands on the right */}
                <MainPageCommands commands={commands}/>
            </div>
        </>
    )
}