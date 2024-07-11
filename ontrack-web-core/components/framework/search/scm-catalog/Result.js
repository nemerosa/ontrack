import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import SCMCatalogEntrySummary from "@components/extension/scm/catalog/SCMCatalogEntrySummary";

export default function Result({data}) {
    return <SearchResultComponent
        title={
            <SCMCatalogEntrySummary
                entry={data.scmCatalogEntry}
                project={data.project}
            />
        }
        description=""
    />
}