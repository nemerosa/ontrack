import Link from "next/link";
import {FaGithub} from "react-icons/fa";
import {Popover, Space, Spin} from "antd";

export default function ValidationRunGitHubWorkflowJobDecorator({decoration}) {

    function Workflow({run}) {
        return (
            <>
                <Popover
                    content={
                        <Space>
                            {`GitHub workflow run: ${run.name}#${run.runNumber}`}
                            {
                                run.running && <Space>
                                    <Spin size="small"/>
                                    Running
                                </Space>
                            }
                        </Space>
                    }
                >
                    <Link href={run.url}>
                        <FaGithub type={
                            run.running ? "warning" : undefined
                        }/>
                    </Link>
                </Popover>
            </>
        )
    }

    return (
        <>
            {
                decoration.data.workflows && decoration.data.workflows.map((run, index) =>
                    <Workflow run={run} key={index}/>
                )
            }
        </>
    )
}