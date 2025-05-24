import {Card} from "antd";

export default function TableFormSection({children}) {
    return (
        <>
            <Card
                size="small"
                className="ot-well"
            >
                {children}
            </Card>
        </>
    )
}