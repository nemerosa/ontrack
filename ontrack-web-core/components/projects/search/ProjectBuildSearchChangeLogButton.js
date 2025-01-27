import {Button, Popover} from "antd";
import {scmChangeLogUri} from "@components/common/Links";
import Link from "next/link";

export default function ProjectBuildSearchChangeLogButton({selectedBuilds}) {
    return (
        <>
            {
                selectedBuilds.length === 2 &&
                <Popover
                    title="Change log between two builds"
                    content="Displays the change log between the two selected builds"
                >
                    <Link href={scmChangeLogUri(selectedBuilds[0].id, selectedBuilds[1].id)} passHref>
                        <Button>
                            Change log
                        </Button>
                    </Link>
                </Popover>
            }
        </>
    )
}