import {useContext, useEffect, useMemo, useState} from "react";
import {Space, Typography} from "antd";
import {useQuery} from "@components/services/GraphQL";
import CloseableAlert from "@components/common/CloseableAlert";
import LoadingContainer from "@components/common/LoadingContainer";
import {ValidationStampFilterContext} from "@components/branches/filters/validationStamps/ValidationStampFilterContext";
import {gqlDeliveryMap} from "@components/branches/views/deliverymap/deliveryMapQueries";
import {
    getLocalDeliveryMapValidationStamps,
    setLocalDeliveryMapValidationStamps,
} from "@components/storage/local";
import {
    applyValidationStampFilter,
    hasNoDependencies,
    isMapEmpty,
    withValidationStamps,
} from "@components/branches/views/deliverymap/deliveryMapModel";
import DeliveryMapHeader from "@components/branches/views/deliverymap/DeliveryMapHeader";
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
 * What it does own is the branch head in its header - the build every checkpoint's lag is counted
 * against - and whether the validation stamps are drawn at all, which is a way of reading this one
 * graph rather than a statement about the branch.
 *
 * @param branch Branch being displayed
 */
export default function DeliveryMapContentView({branch}) {

    const vsfContext = useContext(ValidationStampFilterContext)

    // Starting shown, which is also the stored default, so that the map never lays itself out twice
    // on arrival. The preference is read in an effect rather than at first render because the local
    // storage is not there to be read while the page is being rendered on the server.
    const [showValidationStamps, setShowValidationStamps] = useState(true)
    useEffect(() => {
        setShowValidationStamps(getLocalDeliveryMapValidationStamps())
    }, [])

    const onToggleValidationStamps = () => {
        const shown = !showValidationStamps
        setLocalDeliveryMapValidationStamps(shown)
        setShowValidationStamps(shown)
    }

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
    // against whatever it is given at that moment.
    //
    // Memoised because the graph re-runs the elk layout whenever this changes IDENTITY, and
    // narrowing by a filter builds a new object every time it is called. Without the memo, any
    // re-render of this component while a filter is selected - starting inline edition of that
    // filter, say - would reshuffle the whole map and throw away any node the user had dragged.
    const map = useMemo(
        () => withValidationStamps(
            applyValidationStampFilter(data, vsfContext.selectedFilter),
            showValidationStamps,
        ),
        [data, vsfContext.selectedFilter, showValidationStamps],
    )

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
                            {/* Read on the map the SERVER sent, never on the narrowed one: this
                                notice tells the reader to go and configure auto promotion, and
                                hiding the stamps - or filtering them out - must not make the map
                                ask for configuration which is already there. */}
                            {hasNoDependencies(data) && <DeliveryMapNoDependencies/>}
                            <DeliveryMapHeader head={data?.head}/>
                            <DeliveryMapGraph
                                map={map}
                                showValidationStamps={showValidationStamps}
                                onToggleValidationStamps={onToggleValidationStamps}
                            />
                        </>
                }
            </LoadingContainer>
        </Space>
    )
}
