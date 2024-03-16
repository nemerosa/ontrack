import {Typography} from "antd";
import Duration from "@components/common/Duration";

export default function RunInfoTime({info}) {
    return (
        <>
            {
                info && info.runTime &&
                <Typography.Text>
                    Ran in <Duration
                    seconds={info.runTime}
                    displaySeconds={true}
                    displaySecondsInTooltip={false}
                />
                </Typography.Text>
            }
        </>
    )
}