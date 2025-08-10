import {Typography} from "antd";
import Link from "next/link";

export default function PrCreatingData({data}) {
    // Assuming we have a URL in the data. If we need more details, we
    // delegate to a framework renderer.
    const url = data.url
    if (url) {
        return <Typography.Text>
            Post-processing running at <Link href={url}>{url}</Link>.
        </Typography.Text>
    } else {
        return <Typography.Text>No information about the post-processing.</Typography.Text>
    }
}
