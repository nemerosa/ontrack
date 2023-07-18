import {Space} from "antd";
import Decoration from "@components/framework/decorations/Decoration";

export default function Decorations({entity}) {
    return (
        <>
            {
                entity.decorations && <Space size={8}>
                    {
                        entity.decorations.map(decoration => <Decoration key={decoration.decorationType} decoration={decoration}/>)
                    }
                </Space>
            }
        </>
    )
}