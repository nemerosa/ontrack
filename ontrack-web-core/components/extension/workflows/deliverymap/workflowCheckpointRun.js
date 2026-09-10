/**
 * What a workflow run says on a delivery map, shared by the two kinds which draw one.
 *
 * The promotion side (`workflow`) and the slot side (`slot-workflow`) are separate checkpoint kinds
 * - they have different ids, different owners and different heights - but a run is a run, and the
 * status and the duration have to read identically on both or the reader learns two dialects.
 */

import {Space, Typography, theme} from "antd";
import dayjs from "dayjs";
import utc from 'dayjs/plugin/utc';
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";
import DurationMs from "@components/common/DurationMs";

dayjs.extend(utc)

/**
 * The statuses a workflow instance is finished in, mirroring `WorkflowInstanceStatus.finished` on
 * the server. `STARTED` and `RUNNING` are the two that are not.
 */
const FINISHED = ['SUCCESS', 'ERROR', 'STOPPED']

/**
 * How long a run has taken so far, in milliseconds, or null when there is nothing to say.
 *
 * A finished run reports the duration the engine computed. An unfinished one cannot: `durationMs` is
 * 0 until the LAST node ends, which is why every other workflow UI simply hides it while a workflow
 * is going. The map counts from the start time instead, which is the one thing a running workflow
 * does know.
 *
 * Nothing here ticks. The value moves when the map refreshes - every 60 seconds by default - which
 * is the resolution a checkpoint on a map of a delivery pipeline is read at, and a node counting
 * seconds under the reader's cursor would be the same mistake as relaying out the map on a refresh.
 *
 * @param status Status of the run, absent when it has never run
 * @param startTime When the run started, as the server's UTC timestamp
 * @param durationMs What the engine computed, 0 while the run is unfinished
 * @param now Reference instant, for tests
 */
export function elapsedMs({status, startTime, durationMs}, now = undefined) {
    if (!status) return null
    if (FINISHED.includes(status)) return durationMs ?? null
    if (!startTime) return null
    const elapsed = (now ? dayjs.utc(now) : dayjs.utc()).diff(dayjs.utc(startTime))
    // A clock skew between the server and the reader must not read as a negative age
    return elapsed > 0 ? elapsed : 0
}

/**
 * One line saying where a workflow run got to and how long it has taken.
 *
 * A workflow which has never run says so in words rather than drawing nothing: an empty line and a
 * line still loading would otherwise look alike, which is the same argument `CheckpointArrival`
 * makes for the build it names.
 *
 * @param data The checkpoint's payload
 * @param nothingText What to say when the workflow has never run
 */
export default function WorkflowCheckpointRun({data, nothingText = "Not started"}) {

    const {token} = theme.useToken()
    const {status} = data ?? {}

    if (!status) {
        return <Typography.Text type="secondary" italic>{nothingText}</Typography.Text>
    }

    const ms = elapsedMs(data)

    return (
        <Space size={token.marginXXS}>
            <WorkflowInstanceStatus status={status}/>
            {
                ms !== null &&
                <Typography.Text type="secondary" data-testid="workflow-checkpoint-duration">
                    <DurationMs ms={ms} displaySeconds={false}/>
                </Typography.Text>
            }
        </Space>
    )
}
