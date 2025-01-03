import CloseableAlert from "@components/common/CloseableAlert";
import {Typography} from "antd";

export default function EnvironmentsWarning() {
    return (
        <>
            <CloseableAlert
                id="feature-environments"
                message={
                    <Typography.Text>
                        Management of environments and deployments is still
                        under experiment and assessment. Use with care until
                        first official release.
                    </Typography.Text>
                }
            />
        </>
    )
}