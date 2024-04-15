import CheckIcon from "@components/common/CheckIcon";
import {Tooltip, Typography} from "antd";
import {FaBan, FaClock, FaTimesCircle} from "react-icons/fa";

export default function JobTimeoutIndicator({job}) {
    return (
        <>
            {
                job.lastTimeoutCount < 1 && <Tooltip title="No timeout">
                    <Typography.Text type="secondary">
                        <FaBan/>
                    </Typography.Text>
                </Tooltip>
            }
            {
                job.lastTimeoutCount >= 1 && <Tooltip title={`${job.lastTimeoutCount} timeout(s)`}>
                    <Typography.Text type="danger">
                        <FaClock/>
                    </Typography.Text>
                </Tooltip>
            }
        </>
    )
}