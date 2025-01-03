import {Typography} from "antd";
import CloseableAlert from "@components/common/CloseableAlert";

export default function MainWarning() {
    return (
        <>
            <CloseableAlert
                id="next-ui"
                message={
                    <Typography.Text>
                        You&apos;re accessing Ontrack using its <a
                        href="https://github.com/nemerosa/ontrack/issues/1104">Next UI</a>.
                        This is an experimental feature and your <a
                        href="https://github.com/nemerosa/ontrack/issues/new">feedback</a> is very welcome.
                    </Typography.Text>
                }
            />
        </>
    )
}
