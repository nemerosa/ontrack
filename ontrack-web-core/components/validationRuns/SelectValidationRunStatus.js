import {useRefData} from "@components/providers/RefDataProvider";
import {Select} from "antd";

/**
 *
 * @param statusId ID of a status ID. If not defined, the root statuses are returned.
 * @param all If no [statusId] is defined, and if true, returns all statuses, not only the roots
 * @param value Initial value (ID)
 * @param onChange When selection changes
 */
export default function SelectValidationRunStatus({
                                                      statusId,
                                                      all,
                                                      disabled,
                                                      value,
                                                      onChange
                                                  }) {

    const {validationRunStatuses} = useRefData()

    const rawList = statusId ?
        validationRunStatuses.getAccessibleStatuses(statusId) : (
            all ? validationRunStatuses.list : validationRunStatuses.roots
        )

    const vrsList = rawList.map(vrs => ({
        ...vrs,
        value: vrs.id,
        label: vrs.name,
    }))

    return (
        <>
            <Select
                style={{width: '10em'}}
                disabled={disabled}
                options={vrsList}
                allowClear={true}
                placeholder="Select a status"
                value={value}
                onChange={onChange}
            />
        </>
    )
}