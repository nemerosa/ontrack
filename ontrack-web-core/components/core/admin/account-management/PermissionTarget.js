import {Space, Typography} from "antd";
import PermissionTargetType from "@components/core/admin/account-management/PermissionTargetType";

export default function PermissionTarget({target, displayDescription = true}) {
    return (
        <Space>
            <PermissionTargetType type={target.type}/>
            <Typography.Text>{target.name}</Typography.Text>
            {
                target.description && displayDescription && <Space>
                    <Typography.Text type="secondary">
                        - {target.description}
                    </Typography.Text>
                </Space>
            }
        </Space>
    )
}