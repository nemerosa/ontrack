import {Tag} from "antd";

export default function SlotWorkflowTrigger({trigger}) {
    let text
    switch (trigger) {
        case 'CREATION':
            text = 'Creation'
            break
        case 'DEPLOYING':
            text = 'Deploying'
            break
        case 'DEPLOYED':
            text = 'Deployed'
            break
    }
    return (
        <>
            <Tag>{text}</Tag>
        </>
    )
}