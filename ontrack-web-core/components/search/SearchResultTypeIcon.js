import {Dynamic} from "@components/common/Dynamic";

export default function SearchResultTypeIcon({type}) {
    return <Dynamic path={`framework/search/${type.id}/Icon`}/>
}