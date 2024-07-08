import {Dynamic} from "@components/common/Dynamic";

export default function SearchResult({type, result}) {
    return (
        <>
            <Dynamic path={`framework/search/${type.id}/Result`} props={result}/>
        </>
    )
}