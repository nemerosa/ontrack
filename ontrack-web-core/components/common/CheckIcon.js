import {FaCheck, FaTimes} from "react-icons/fa";

export default function CheckIcon({id = "check", value}) {
    return (
        <>
            {
                value ?
                    <FaCheck data-testid={`${id}-ok`} color="green"/> :
                    <FaTimes data-testid={`${id}-nok`} color="red"/>
            }
        </>
    )
}