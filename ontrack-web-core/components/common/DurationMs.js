import Duration from "@components/common/Duration";

export default function DurationMs({
                                       ms,
                                       threshold = 5000,
                                       displaySeconds = true,
                                       displaySecondsInTooltip = true,
                                   }) {
    if (ms !== undefined) {
        if (ms < threshold) {
            return `${ms} ms`
        } else {
            return <Duration
                seconds={Math.floor(ms / 1000)}
                displaySeconds={displaySeconds}
                displaySecondsInTooltip={displaySecondsInTooltip}
            />
        }
    } else {
        return ''
    }
}