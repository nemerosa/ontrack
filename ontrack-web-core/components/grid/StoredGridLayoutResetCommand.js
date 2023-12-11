import {Command} from "@components/common/Commands";
import {FaWindowRestore} from "react-icons/fa";
import {useContext} from "react";
import {StoredGridLayoutContext} from "@components/grid/StoredGridLayoutContext";

export default function StoredGridLayoutResetCommand({text, icon, title}) {

    const {resetLayout} = useContext(StoredGridLayoutContext)

    return (
        <>
            <Command
                text={text ?? "Reset layout"}
                title={title ?? "Resets the layout of the page."}
                icon={icon ?? <FaWindowRestore/>}
                action={resetLayout}
            />
        </>
    )
}