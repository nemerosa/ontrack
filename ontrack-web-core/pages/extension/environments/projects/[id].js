import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import ProjectEnvironmentsView from "@components/extension/environments/project/ProjectEnvironmentsView";

export default function ProjectEnvironmentsPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <ProjectEnvironmentsView id={Number(id)}/>
                </MainLayout>
            </main>
        </>
    )
}