import {Space, Typography} from "antd";
import Link from "next/link";

export default function SearchResultComponent({title, link, description}) {
    return (
        <>
            <Space direction="vertical">
                {
                    link &&
                    <Link href={link}>{title}</Link>
                }
                {
                    !link && title
                }
                <Typography.Paragraph type="secondary">{description}</Typography.Paragraph>
            </Space>
        </>
    )
}