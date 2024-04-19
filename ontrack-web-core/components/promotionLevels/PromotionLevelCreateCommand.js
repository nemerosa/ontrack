import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";

export default function PromotionLevelCreateCommand() {
    return (
        <>

            <Command
                icon={<FaPlus/>}
                text="Create promotion level"
                // action={onChangeImage}
            />
        </>
    )
}