import {Tooltip} from "antd";
import Link from "next/link";
import {FaJenkins} from "react-icons/fa";

export default function JenkinsJobDecorationExtension({decoration}) {
    return (
        <>
            <Tooltip title="Link to Jenkins job">
                <Link href={decoration.data.url}>
                    <FaJenkins/>
                </Link>
            </Tooltip>
        </>
    )
}