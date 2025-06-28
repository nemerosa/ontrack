import {Typography} from "antd";

export default function PrCreatingData({data}) {
    return <Typography.Text>
        Processing was aborted: {data.message}
    </Typography.Text>
}
