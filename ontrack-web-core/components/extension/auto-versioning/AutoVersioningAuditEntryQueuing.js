import {Space, Tooltip, Typography} from "antd";
import {FaArrowRight} from "react-icons/fa";

export default function AutoVersioningAuditEntryQueuing({entry}) {
    return (
        <>
            <Space>
                <Tooltip title="Routing key">
                    <Typography.Text>{entry.routing}</Typography.Text>
                </Tooltip>
                {
                    entry.queue &&
                    <>
                        <FaArrowRight/>
                        <Tooltip title="Queue name">
                            <Typography.Text>{entry.queue}</Typography.Text>
                        </Tooltip>
                    </>
                }
            </Space>
            {/*

            <span>{{ item.routing }}</span>
            <span ng-if="item.queue">
                <i class="fa fa-arrow-right"></i>&nbsp;{{ item.queue }}
            </span>
        */}
        </>
    )
}