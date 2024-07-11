import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import LegacyLink from "@components/common/LegacyLink";

export default function Result({data, page}) {
    return <SearchResultComponent
        title={
            <LegacyLink href={page}>{data.item.displayKey}</LegacyLink>
        }
        description=""
    />
}