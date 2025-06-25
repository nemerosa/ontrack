import {Dynamic} from "@components/common/Dynamic";

export default function LicenseFeatureData({featureId, featureData}) {

    const featureDataObject = {}
    for (const {name, value} of featureData) {
        featureDataObject[name] = value
    }

    return (
        <>
            <Dynamic
                path={`framework/license-feature-data/${featureId}/Info`}
                props={featureDataObject}
            />
        </>
    )
}