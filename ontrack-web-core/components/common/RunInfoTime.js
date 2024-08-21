import {Typography} from "antd";
import Duration from "@components/common/Duration";

export default function RunInfoTime({info, mode = "complete"}) {
    return (
        <>
            {
                info && info.runTime && mode === "complete" &&
                <Typography.Text>
                    Ran in <Duration
                    seconds={info.runTime}
                    displaySeconds={true}
                    displaySecondsInTooltip={false}
                />
                </Typography.Text>
            }
            {
                info && info.runTime && mode === "minimal" &&
                <Typography.Text>
                    <Duration
                        seconds={info.runTime}
                        displaySeconds={true}
                        displaySecondsInTooltip={true}
                    />
                </Typography.Text>
            }
        </>
    )
}