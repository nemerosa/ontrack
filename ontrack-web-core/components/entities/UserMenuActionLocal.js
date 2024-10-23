import {getUserMenuItemExtensionName} from "@components/common/ExtensionUtils";
import {Dynamic} from "@components/common/Dynamic";

export default function UserMenuActionLocal({item}) {

    const args = item.arguments ?? {}
    const extensionName = getUserMenuItemExtensionName(item.extension)

    return (
        <>
            <Dynamic
                path={`framework/user-menu-action/${extensionName}/${item.id}`}
                props={{...args}}
            />
        </>
    )
}