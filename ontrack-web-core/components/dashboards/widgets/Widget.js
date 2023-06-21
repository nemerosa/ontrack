import {Card} from "antd";

export default function Widget({title, children}) {
    return (
        <Card
            title={title}
            headStyle={{
                backgroundColor: 'lightgrey'
            }}
            style={{
                width: '100%'
            }}
        >
            {children}
        </Card>
    )
}