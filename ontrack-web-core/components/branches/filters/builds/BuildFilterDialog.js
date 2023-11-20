import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Dynamic} from "@components/common/Dynamic";

/**
 * Dialog to create/edit a filter. Its context has the following properties:
 *
 * * branch - the branch for the filter
 * * buildFilterForm - the form for the filter
 * * buildFilterData - the filter data (undefined for a creation)
 * * buildFilterName - the filter name (undefined for the edition of an anonymous filter and for a creation)
 */

export function useBuildFilterDialog({onFilterSuccess}) {
    return useFormDialog({
        init: (form, context) => {
            const {buildFilterData, buildFilterName} = context
            form.setFieldValue(['data'], buildFilterData)
            form.setFieldValue(['data', 'name'], buildFilterName)
        },
        onSuccess: onFilterSuccess,
    })
}

export default function BuildFilterDialog({buildFilterDialog}) {
    return (
        <>
            <FormDialog dialog={buildFilterDialog}>
                {
                    buildFilterDialog.context && buildFilterDialog.context.branch &&
                    <Dynamic
                        path={`framework/build-filter/${buildFilterDialog.context?.buildFilterForm?.type}`}
                        props={{
                            branch: buildFilterDialog.context.branch,
                            buildFilterForm: buildFilterDialog.context.buildFilterForm,
                        }}
                    />
                }
            </FormDialog>
        </>
    )
}