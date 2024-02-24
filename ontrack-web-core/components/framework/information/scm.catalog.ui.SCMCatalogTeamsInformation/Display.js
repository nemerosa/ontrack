import {List, Space, Typography} from "antd";
import Link from "next/link";

export default function SCMCatalogTeamsInformation({info}) {
    return <List
        dataSource={info.data}
        renderItem={(team) => (
            <List.Item>
                <Space>
                    {
                        team.url && <Link href={team.url}>
                            <Typography.Text>{team.name}</Typography.Text>
                        </Link>
                    }
                    {
                        !team.url && <Typography.Text>{team.name}</Typography.Text>
                    }
                    {
                        team.role && <Typography.Text disabled>{team.role}</Typography.Text>
                    }
                </Space>
            </List.Item>
        )}
    />
}