import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import BuildView from "@components/builds/BuildView";

export default function BuildPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <BuildView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}