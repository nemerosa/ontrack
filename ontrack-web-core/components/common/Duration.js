import {Popover, Typography} from "antd";
import dayjs from "dayjs";
import * as duration from 'dayjs/plugin/duration';
import * as relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(duration);
dayjs.extend(relativeTime);

export default function Duration({
                                     seconds,
                                     displaySeconds = true,
                                     displaySecondsInTooltip = true,
                                 }) {
    if (!seconds && seconds !== 0) {
        return ''
    } else {
        const inner = dayjs.duration(seconds, "seconds").humanize()
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