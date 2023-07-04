import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import ProjectView from "@components/views/ProjectView";

export default function ProjectPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <ProjectView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}