import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import {Space} from "antd";
import FQBuildLink from "@components/builds/FQBuildLink";
import Display from "@components/framework/properties/general.ReleasePropertyType/Display";

export default function Result({data}) {
    return <SearchResultComponent
        title={
            <Space>
                <FQBuildLink build={data.build}/>
                <Display property={{value: {name: data.release}}}/>
            </Space>
        }
        description=""
    />
}