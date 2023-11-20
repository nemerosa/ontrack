import BuildDependency from "@components/builds/BuildDependency";
import {Space} from "antd";

export default function BuildDependencies({pageItems, pageInfo, displayPromotions}) {
    return (
        <Space>
            {
                pageItems.map(link => (
                    <>
                        <BuildDependency link={link} displayPromotions={displayPromotions}/>
                    </>
                ))
            }
            {/*  TODO If pageInfo.next, displays an ellipsis  */}
        </Space>
    )
}