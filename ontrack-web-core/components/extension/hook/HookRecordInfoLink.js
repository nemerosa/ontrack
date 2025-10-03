import {Dynamic} from "@components/common/Dynamic";

export default function HookRecordInfoLink({infoLink}) {
    return (
        <>
            {
                infoLink &&
                <Dynamic
                    path={`framework/hook-info-link/${infoLink.feature}/${infoLink.id}/Info`}
                    props={{data: infoLink.data}}
                />
            }
        </>
    )
}