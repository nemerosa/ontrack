import {Typography} from "antd";

export default function Display({property}) {

    return (
        <>
            {
                property.value && <>
                    {
                        property.value.previousPromotionRequired &&
                        <Typography.Text>Depends on previous promotion to be granted.</Typography.Text>
                    }
                    {
                        !property.value.previousPromotionRequired &&
                        <Typography.Text>Does not depend on previous promotion to be granted.</Typography.Text>
                    }
                </>
            }
        </>
    )
}