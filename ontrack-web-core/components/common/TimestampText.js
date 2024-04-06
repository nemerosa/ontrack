import dayjs from "dayjs";

import * as utc from 'dayjs/plugin/utc';
import * as timezone from 'dayjs/plugin/timezone';

dayjs.extend(utc);
dayjs.extend(timezone);

export const weekDayFormat = "ddd, MMM DD, YYYY, HH:mm:ss"

export default function TimestampText({
                                          value,
                                          prefix = '',
                                          suffix = '',
                                          format = "YYYY MMM DD, HH:mm",
                                      }) {

    const localDateTime = dayjs.utc(value).local()

    return (
        <>

            {prefix && `${prefix} `}
            {localDateTime.format(format)}
            {suffix && ` ${suffix}`}
        </>
    )
}