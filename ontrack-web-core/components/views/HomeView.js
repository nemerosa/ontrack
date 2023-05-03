import MainPage from "@components/layouts/MainPage";
import ProjectList from "@components/projects/ProjectList";

export default function HomeView() {
    return (
        <>
            <MainPage
                title="Home"
            >
                <ProjectList/>
            </MainPage>
        </>
    )
}