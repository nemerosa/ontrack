import {Popover} from "antd";
import dayjs from "dayjs";
import * as duration from 'dayjs/plugin/duration';
import * as relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(duration);
dayjs.extend(relativeTime);

export default function Duration({seconds, displaySeconds = true}) {
    if (!seconds && seconds !== 0) {
        return ''
    } else {
        const inner = dayjs.duration(seconds, "seconds").humanize()
        if (displaySeconds) {
            return <Popover content={`${seconds} second${seconds > 1 ? 's' : ''}`}>{inner}</Popover>
        } else {
            return inner
        }
    }
}