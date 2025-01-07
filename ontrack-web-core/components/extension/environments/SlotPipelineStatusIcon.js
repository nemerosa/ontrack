import {FaBan, FaCheck, FaPlay, FaSpinner} from "react-icons/fa";

const icons = {
    CANDIDATE: <FaSpinner color="blue"/>,
    RUNNING: <FaPlay color="blue"/>,
    CANCELLED: <FaBan color="gray"/>,
    DONE: <FaCheck color="green"/>,
}

export default function SlotPipelineStatusIcon({status}) {
    return icons[status]
}