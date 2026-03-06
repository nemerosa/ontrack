import {Space, Typography} from "antd";
import YesNo from "@components/common/YesNo";

export default function OntrackValidationNotificationChannelConfig({project, branch, build, promotion, waitForPromotion, waitForPromotionTimeout}) {
    return (
        <>
            <Space direction="vertical">
                <Space>
                    Project:
                    <Typography.Text code>{project}</Typography.Text>
                </Space>
                <Space>
                    Branch:
                    <Typography.Text code>{branch}</Typography.Text>
                </Space>
                <Space>
                    Build:
                    <Typography.Text code>{build}</Typography.Text>
                </Space>
                <Space>
                    Promotion:
                    <Typography.Text code>{promotion}</Typography.Text>
                </Space>
                <Space>
                    Waiting:
                    <YesNo value={waitForPromotion}/>
                </Space>
                <Space>
                    Waiting timeout:
                    <Typography.Text code>{waitForPromotionTimeout}</Typography.Text>
                </Space>
            </Space>
        </>
    )
}