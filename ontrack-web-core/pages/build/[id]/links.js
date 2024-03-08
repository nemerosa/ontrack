import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import BuildLinksView from "@components/links/BuildLinksView";

export default function BuildLinksPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <BuildLinksView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}