import {Popover, Typography} from "antd";
import dayjs from "dayjs";
import duration from 'dayjs/plugin/duration';
import relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(duration)
dayjs.extend(relativeTime)

export const formatSeconds = (seconds, nanValue = "-") => {
    if (typeof seconds === 'number' && !isNaN(seconds)) {
        if (seconds < 0) {
            return nanValue
        } else if (seconds < 60) {
            if (seconds <= 1) {
                return `${seconds} second`
            } else {
                return `${seconds} seconds`
            }
        } else {
            return dayjs.duration(seconds, "seconds").humanize()
        }
    } else {
        return nanValue
    }
}

export default function Duration({
                                     seconds,
                                     displaySeconds = true,
                                     displaySecondsInTooltip = true,
                                     defaultText = ''
                                 }) {
    if (!seconds && seconds !== 0) {
        return defaultText
    } else {
        const inner = formatSeconds(seconds)
        if (displaySeconds) {
            const secondsText = `${seconds} second${seconds > 1 ? 's' : ''}`
            if (displaySecondsInTooltip) {
                return <Popover content={secondsText}>{inner}</Popover>
            } else {
                return <Typography.Text>
                    {inner} ({secondsText})
                </Typography.Text>
            }
        } else {
            return inner
        }
    }
}