import {FaBitbucket} from "react-icons/fa";
import {Typography} from "antd";

export default function BitBucketProjectDecorator({decoration}) {
    return (
        <>
            <Typography.Text>
                <FaBitbucket/>
            </Typography.Text>
        </>
    )
}