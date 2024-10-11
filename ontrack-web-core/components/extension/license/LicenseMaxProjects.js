import {Typography} from "antd";

export default function LicenseMaxProjects({maxProjects}) {
    return (
        <>
            {
                maxProjects === 0 && <Typography.Text>No project limit.</Typography.Text>
            }
            {
                maxProjects > 0 && <Typography.Text>Max. {maxProjects} projects.</Typography.Text>
            }
        </>
    )
}