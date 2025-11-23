import ConfirmCommand from "@components/common/ConfirmCommand";
import {FaTrash} from "react-icons/fa";
import {gql} from "graphql-request";
import {buildUri} from "@components/common/Links";
import {useRouter} from "next/router";

export default function PromotionRunDeleteCommand({run}) {

    const router = useRouter()

    const goToBuild = async () => {
        console.log(`[promotion-run] Going to run ${run}`)
        console.log(`[promotion-run] Going to build ${run.build}`)
        console.log(`[promotion-run] Going to build ID ${run.build.id}`)
        await router.push(buildUri(run.build))
    }

    return (
        <>
            <ConfirmCommand
                key="delete"
                icon={<FaTrash/>}
                text="Delete"
                confirmTitle="Removing this promotion run"
                confirmText="Do you really want to remove this promotion run?"
                confirmOkText="Confirm deletion"
                gqlQuery={
                    gql`
                        mutation DeletePromotionRun($id: Int!) {
                            deletePromotionRun(input: {promotionRunId: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `
                }
                gqlVariables={{id: Number(run.id)}}
                gqlUserNode="deletePromotionRun"
                onSuccess={goToBuild}
            />
        </>
    )
}