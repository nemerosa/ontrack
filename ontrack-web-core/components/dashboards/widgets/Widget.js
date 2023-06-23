import {Card, Skeleton} from "antd";

export default function Widget({title, loading, children}) {
    return (
        <Card
            title={loading ? "Loading..." : title}
            headStyle={{
                backgroundColor: 'lightgrey'
            }}
            style={{
                width: '100%'
            }}
        >
            {loading && <Skeleton active/>}
            {!loading && children}
        </Card>
    )
}