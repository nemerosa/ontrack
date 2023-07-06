import ProjectList, {useProjectList} from "@components/projects/ProjectList";

export default function HomeView() {

    const projectList = useProjectList()

    return (
        <>
            <p>TODO Dashboard page</p>
            <ProjectList projectList={projectList}/>
        </>
    )
}