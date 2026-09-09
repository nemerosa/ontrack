import {useContext} from "react";
import {Space, Typography} from "antd";
import {useQuery} from "@components/services/GraphQL";
import CloseableAlert from "@components/common/CloseableAlert";
import LoadingContainer from "@components/common/LoadingContainer";
import {ValidationStampFilterContext} from "@components/branches/filters/validationStamps/ValidationStampFilterContext";
import {gqlDeliveryMap} from "@components/branches/views/deliverymap/deliveryMapQueries";
import {
    applyValidationStampFilter,
    hasNoDependencies,
    isMapEmpty,
} from "@components/branches/views/deliverymap/deliveryMapModel";
import DeliveryMapGraph from "@components/branches/views/deliverymap/DeliveryMapGraph";
import DeliveryMapEmpty from "@components/branches/views/deliverymap/DeliveryMapEmpty";
import DeliveryMapNoDependencies from "@components/branches/views/deliverymap/DeliveryMapNoDependencies";

/**
 * The delivery map branch content view: what a build on this branch has to pass through on its way
 * to an environment.
 *
 * A peer of the builds and pipeline views on the single axis of choice established by ADR 0001, and
 * like the pipeline view it does NOT own the validation stamp filter, which lives above the view
 * switch so that a user's filter follows them from one view to the next. It reads it and narrows the
 * map by it.
 *
 * @param branch Branch being displayed
 */
export default function DeliveryMapContentView({branch}) {

    const vsfContext = useContext(ValidationStampFilterContext)

    const {data, loading, finished} = useQuery(
        gqlDeliveryMap,
        {
            variables: {branchId: Number(branch.id)},
            deps: [branch],
            initialData: null,
            dataFn: data => data.branch?.deliveryMap,
        }
    )

    // Derived from what is already in hand, not stored: a `useState` filled by an effect would leave
    // the graph laid out against an empty map for one render, and React Flow syncs its own layout
    // against whatever it is given at that moment
    const map = applyValidationStampFilter(data, vsfContext.selectedFilter)

    return (
        <Space direction="vertical" size={16} className="ot-line">
            {/* The loud half of the experimental marking, as the pipeline view does it: dismissible,
                and carrying the invitation to give feedback. `info` rather than `warning` - the view
                is new, not risky, and a yellow band argues against adopting what we are asking
                people to adopt. */}
            <CloseableAlert
                id="feature-branch-delivery-map-view"
                type="info"
                message={
                    <Typography.Text data-testid="delivery-map-experimental-alert">
                        The Delivery map view is experimental and still being refined. Your feedback
                        is welcome — tell us what works and what does not in{' '}
                        <a href="https://github.com/yontrack/yontrack/discussions"
                           target="_blank" rel="noreferrer">
                            GitHub Discussions
                        </a>.
                    </Typography.Text>
                }
            />
            {/* `useQuery` starts with `loading` false and only flips it inside its effect, so a view
                which must never render as "loaded" before the first fetch resolves reads both */}
            <LoadingContainer loading={loading || !finished}>
                {
                    isMapEmpty(map) ?
                        <DeliveryMapEmpty/> :
                        <>
                            {hasNoDependencies(map) && <DeliveryMapNoDependencies/>}
                            <DeliveryMapGraph map={map}/>
                        </>
                }
            </LoadingContainer>
        </Space>
    )
}
