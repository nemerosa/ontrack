import {Button, Input, Typography} from "antd";
import Columns from "@components/common/Columns";
import {FaComment, FaPlus} from "react-icons/fa";
import SelectValidationRunStatus from "@components/validationRuns/SelectValidationRunStatus";
import Rows from "@components/common/Rows";
import {useState} from "react";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

/**
 * @param run Validation run to update
 * @param onStatusChanged Called with the run ID when the status has changed
 */
export default function ValidationRunStatusChange({run, onStatusChanged}) {

    const client = useGraphQLClient()

    const [nextStatusId, setNextStatusId] = useState('')
    const [description, setDescription] = useState('')

    const changeStatus = async () => {
        if (nextStatusId) {
            await client.request(
                gql`
                    mutation ChangeValidationRunStatus(
                        $runId: Int!,
                        $statusId: String!,
                        $description: String,
                    ) {
                        changeValidationRunStatus(input: {
                            validationRunId: $runId,
                            validationRunStatusId: $statusId,
                            description: $description,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `, {
                    runId: Number(run.id),
                    statusId: nextStatusId,
                    description,
                }
            )
            if (onStatusChanged) {
                onStatusChanged(run.id)
            }
        }
    }

    return (
        <>
            <Rows>
                <Columns>
                    <Typography.Text>
                        <FaComment/>
                        &nbsp;
                        Change the status of the run by setting a new status
                        and an optional description.
                    </Typography.Text>
                </Columns>
                <Columns>
                    <SelectValidationRunStatus
                        id={run.id}
                        statusId={run.lastStatus.statusID.id}
                        onChange={setNextStatusId}
                    />
                    <Input
                        placeholder="Optional description"
                        disabled={!nextStatusId}
                        style={{
                            width: '25em',
                        }}
                        onChange={e => setDescription(e.target.value)}
                    />
                    <Button
                        icon={<FaPlus/>}
                        disabled={!nextStatusId}
                        onClick={changeStatus}
                    >
                        Add
                    </Button>
                </Columns>
            </Rows>
        </>
    )
}