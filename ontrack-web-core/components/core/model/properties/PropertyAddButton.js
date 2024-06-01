import {Button} from "antd";
import {FaPlus} from "react-icons/fa";
import PropertyDialog, {usePropertyDialog} from "@components/core/model/properties/PropertyDialog";

export default function PropertyAddButton({entityType, entityId, propertyList}) {

    const dialog = usePropertyDialog({})

    const startDialog = () => {
        dialog.start({entityType, entityId, propertyList})
    }

    return (
        <>
            <Button size="small" icon={<FaPlus/>} title="Add a property" onClick={startDialog}/>
            <PropertyDialog dialog={dialog}/>
        </>
    )
}