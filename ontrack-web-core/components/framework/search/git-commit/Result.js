import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import LegacyLink from "@components/common/LegacyLink";
import {Typography} from "antd";

export default function Result({page, data}) {
    return <SearchResultComponent
        title={
            <LegacyLink href={page}><Typography.Text code>{data.item.commitShort}</Typography.Text></LegacyLink>
        }
        description={data.item.commitMessage}
    />
}