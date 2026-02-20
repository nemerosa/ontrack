import {Typography} from "antd";

export default function AutoVersioningSchedule({schedule}) {
    if (schedule) {
        return <Typography.Text data-testid="auto-versioning-schedule" code>{schedule}</Typography.Text>
    } else {
        return "-"
    }
}