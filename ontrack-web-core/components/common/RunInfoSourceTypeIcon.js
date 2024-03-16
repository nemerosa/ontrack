import {FaJenkins} from "react-icons/fa";

export default function RunInfoSourceTypeIcon({type}) {
    return (
        <>
            {
                type === 'jenkins' && <FaJenkins/>
            }
        </>
    )
}