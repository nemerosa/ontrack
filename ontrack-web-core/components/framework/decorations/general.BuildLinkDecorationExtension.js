import {Space} from "antd";

function BuildLinkDecoration({link}) {
    return (
        <Space>
            {/* TODO */}
        </Space>
    )
}

export default function BuildLinkDecorationExtension({decoration}) {
    return (
        <>
            {
                decoration.data.decorations.map((link, index) => <BuildLinkDecoration
                    key={index}
                    link={link}
                />)
            }
        </>
    )
}