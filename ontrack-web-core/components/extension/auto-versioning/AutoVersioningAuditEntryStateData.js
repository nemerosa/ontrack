import {Dynamic} from "@components/common/Dynamic";

export default function AutoVersioningAuditEntryStateData({order, state, data}) {
    return (
        <>
            <Dynamic
                path={`framework/auto-versioning-audit-state-data/${state}/Data.js`}
                props={{order, data}}
            />
        </>
    )
}