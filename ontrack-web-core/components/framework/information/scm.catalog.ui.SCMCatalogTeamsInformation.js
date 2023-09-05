import {List, Space, Typography} from "antd";
import Link from "next/link";
import Section from "@components/common/Section";

export default function SCMCatalogTeamsInformation({info}) {
    return (
        <>
            <Section title="SCM teams">
                <List
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
            </Section>
        </>
    )
}