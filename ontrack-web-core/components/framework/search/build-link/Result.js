import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import {Space, Typography} from "antd";
import FQBuildLink from "@components/builds/FQBuildLink";
import {FaArrowRight} from "react-icons/fa";

export default function Result({data}) {
    return <SearchResultComponent
        title={
            <Space>
                <FQBuildLink build={data.sourceBuild}/>
                <FaArrowRight/>
                {
                    data.qualifier &&
                    <>
                        [<Typography.Text type="secondary">{data.qualifier}</Typography.Text>]
                        <FaArrowRight/>
                    </>
                }
                <FQBuildLink build={data.targetBuild}/>
            </Space>
        }
        description=""
    />
}