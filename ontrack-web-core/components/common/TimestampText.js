import dayjs from "dayjs";

export const weekDayFormat = "ddd, MMM DD, YYYY, HH:mm:ss"

export default function TimestampText({
                                          value,
                                          prefix = '',
                                          suffix = '',
                                          format = "YYYY MMM DD, HH:mm",
                                      }) {
    return (
        <>

            {prefix && `${prefix} `}
            {dayjs(value).format(format)}
            {suffix && ` ${suffix}`}
        </>
    )
}