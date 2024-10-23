import {Typography} from "antd";
import BuildStartDeploymentDialog, {
    useBuildStartDeploymentDialog
} from "@components/extension/environments/BuildStartDeploymentDialog";

export default function EnvironmentsBuildStartPipeline({buildId}) {

    const dialog = useBuildStartDeploymentDialog({buildId})

    const onClick = () => {
        dialog.start({})
    }

    return (
        <>
            <Typography.Text onClick={onClick}>Start deployment</Typography.Text>
            <BuildStartDeploymentDialog dialog={dialog}/>
        </>
    )
}
