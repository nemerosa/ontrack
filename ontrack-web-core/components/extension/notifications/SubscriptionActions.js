import {Typography} from "antd";
import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {useDeleteSubscription} from "@components/extension/notifications/DeleteSubscription";

export const useSubscriptionActions = (entity, managePermission, reload) => {
    const {deleteSubscription} = useDeleteSubscription()

    const onDeleteSubscription = (item) => {
        return async () => {
            await deleteSubscription({
                name: item.name,
                entity: entity,
            })
            if (reload) reload()
        }
    }

    const getActions = (item) => {
        const actions = []

        if (item.disabled) {
            actions.push(
                <Typography.Text type="secondary">Disabled</Typography.Text>
            )
        }

        // Renaming & deleting a subscription
        if (managePermission) {
            actions.push(
                <InlineConfirmCommand
                    key="delete"
                    title="Deletes the subscription"
                    confirm="Do you really want to delete this subscription?"
                    onConfirm={onDeleteSubscription(item)}
                />
            )
        }

        return actions
    }


    return {
        getActions,
    }
}