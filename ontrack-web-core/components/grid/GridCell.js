import {Button, Card, Space, Spin, Typography} from "antd";
import {FaArrowsAlt} from "react-icons/fa";

export default function GridCell({title, loading, padding = true, extra, children}) {

    const bodyStyle = {}
    if (!padding) {
        bodyStyle.padding = 0
    }

    return (
        <>
            <Card
                title={loading ? "Loading..." : title}
                bodyStyle={bodyStyle}
                headStyle={{
                    backgroundColor: 'lightgrey'
                }}
                style={{
                    width: '100%',
                    height: '100%',
                }}
                extra={
                    <Space>
                        {extra}
                        <Button
                            title="Use this handle to drag the widget into another position"
                            className="ot-rgl-draggable-handle"
                        >
                            <FaArrowsAlt/>
                        </Button>
                    </Space>
                }
            >
                {
                    !loading && children
                }
                {
                    loading &&
                    <Space>
                        <Spin/>
                        <Typography.Text>Loading...</Typography.Text>
                    </Space>
                }
            </Card>
        </>
    )
}
