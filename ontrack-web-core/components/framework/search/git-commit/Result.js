import SearchResultComponent from "@components/framework/search/SearchResultComponent";

export default function Result({page, data}) {
    return <SearchResultComponent
        title="TODO Page missing"
        description={data.item.commitMessage}
    />
}