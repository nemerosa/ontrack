import SearchResultComponent from "@components/framework/search/SearchResultComponent";

export default function Result({data}) {
    return <SearchResultComponent
        title="TODO"
        description={JSON.stringify(data)}
    />
}