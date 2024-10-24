import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import SlotPipelineView from "@components/extension/environments/SlotPipelineView";

export default function SlotPipelinePage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <SlotPipelineView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}