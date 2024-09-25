import Rows from "@components/common/Rows";
import {isAuthorized} from "@components/common/authorizations";
import ValidationRunStatusChange from "@components/validationRuns/ValidationRunStatusChange";
import ValidationRunData from "@components/framework/validation-run-data/ValidationRunData";
import RunInfo from "@components/common/RunInfo";
import ValidationRunStatusList from "@components/validationRuns/ValidationRunStatusList";

export default function ValidationRun({run, onStatusChanged, onRunChanged}) {
    return (
        <>
            <Rows>
                {/* Adding a comment */}
                {
                    isAuthorized(run, 'validation_run', 'status_change') &&
                    <ValidationRunStatusChange
                        run={run}
                        onStatusChanged={onStatusChanged}
                    />
                }
                {/* Validation run data */}
                {
                    run.data &&
                    <ValidationRunData data={run.data}/>
                }
                {/* Run info */}
                {
                    run.runInfo &&
                    <RunInfo info={run.runInfo}/>
                }
                {/* List of statuses */}
                <ValidationRunStatusList
                    run={run}
                    onRunChanged={onRunChanged}
                />
            </Rows>
        </>
    )
}