import {Alert, Typography} from "antd";

export default function MainWarning() {
    return (
        <>
            <Alert
                message={
                    <Typography.Text>
                        You're accessing Ontrack using its <a href="https://github.com/nemerosa/ontrack/issues/1104">Next UI</a>.
                        This is an experimental feature and your <a href="https://github.com/nemerosa/ontrack/issues/new">feedback</a> is very welcome.
                    </Typography.Text>
                }
                type="warning"
            />
        </>
    )
}