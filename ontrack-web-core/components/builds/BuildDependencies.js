import BuildDependency from "@components/builds/BuildDependency";
import {Space} from "antd";

export default function BuildDependencies({size, pageItems, pageInfo, displayPromotions}) {
    return (
        <Space size={size}>
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