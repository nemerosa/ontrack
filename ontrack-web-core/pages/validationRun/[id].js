import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import ValidationRunView from "@components/validationRuns/ValidationRunView";

export default function ValidationRunPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <ValidationRunView id={Number(id)}/>
                </MainLayout>
            </main>
        </>
    )
}