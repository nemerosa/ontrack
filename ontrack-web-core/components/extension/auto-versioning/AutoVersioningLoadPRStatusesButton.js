import {Button} from "antd";
import {FaDownload} from "react-icons/fa";

export default function AutoVersioningLoadPRStatusesButton({onClick}) {
    return (
        <>
            <Button
                icon={<FaDownload/>}
                onClick={onClick}
            >
                Load PR statuses
            </Button>
        </>
    )
}