import {List, Typography} from "antd";
import TimestampText from "@components/common/TimestampText";

export default function QueueRecordHistory({record}) {
    return (
        <>
            <List
                style={{
                    width: "100%",
                }}
                dataSource={record.history}
                renderItem={(item) =>
                    <List.Item>
                        <List.Item.Meta
                            title={
                                <Typography.Text code>{item.state}</Typography.Text>
                            }
                            description={
                                <TimestampText value={item.time}/>
                            }
                        />
                    </List.Item>
                }
            />
        </>
    )
}