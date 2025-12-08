import TimestampText from "@components/common/TimestampText";

export default function AutoVersioningSchedule({schedule}) {
    if (schedule) {
        return <TimestampText value={schedule}/>
    } else {
        return "-"
    }
}