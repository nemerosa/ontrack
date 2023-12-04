import {Command} from "@components/common/Commands";
import {FaWindowRestore} from "react-icons/fa";
import {useContext} from "react";
import {GridLayoutContext} from "@components/grid/GridLayoutContextProvider";

export default function GridLayoutResetCommand({text, icon, title}) {

    const context = useContext(GridLayoutContext)

    const reset = () => {
        if (context) {
            context.resetLayout()
        }
    }

    return (
        <>
            <Command
                text={text ?? "Reset layout"}
                title={title ?? "Resets the layout of the page."}
                icon={icon ?? <FaWindowRestore/>}
                action={reset}
            />
        </>
    )
}