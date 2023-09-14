import {useRefData} from "@components/providers/RefDataProvider";
import {Select} from "antd";

/**
 *
 * @param statusId ID of a status ID. If not defined, the root statuses are returned.
 * @param value Initial value (ID)
 * @param onChange When selection changes
 */
export default function SelectValidationRunStatus({
                                                      statusId,
                                                      value,
                                                      onChange
                                                  }) {

    const {validationRunStatuses} = useRefData()

    const vrsList = statusId ?
        validationRunStatuses.getAccessibleStatuses(statusId).map(vrs => ({
            ...vrs,
            value: vrs.id,
            label: vrs.name,
        })) :
        [] // TODO Roots

    return (
        <>
            <Select
                style={{width: '10em'}}
                options={vrsList}
                allowClear={true}
                placeholder="Select a status"
                value={value}
                onChange={onChange}
            />
        </>
    )
}