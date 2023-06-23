import {Card} from "antd";
import {branchLink} from "@components/common/Links";

const {Meta} = Card;

export default function BranchBox({branch}) {
    return (
        <>
            <Card>
                <Meta
                    // avatar={<Favourite value={project.favourite}/>}
                    title={branchLink(branch)}
                />
            </Card>
        </>
    )
}