import {FaCheck, FaTimes} from "react-icons/fa";

export default function CheckIcon({value}) {
    return (
        <>
            {
                value ?
                    <FaCheck color="green"/> :
                    <FaTimes color="red"/>
            }
        </>
    )
}