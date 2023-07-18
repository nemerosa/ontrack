import {Tooltip, Typography} from "antd";
import {FaSuitcase} from "react-icons/fa";

export default function SCMCatalogEntryOrphanProjectDecoration({decoration}) {
    return (
        <Tooltip title="This project is not associated with any SCM entry or its entry is obsolete. This usually shows a misconfiguration.">
            <Typography.Text type="danger">
                <FaSuitcase/>
            </Typography.Text>
        </Tooltip>
    )
}