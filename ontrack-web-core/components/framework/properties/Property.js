import {Card} from "antd";
import {FaSpinner} from "react-icons/fa";
import {lazy, useEffect, useState} from "react";

const {Meta} = Card

export default function Property({property}) {
    const shortTypeName = property.type.typeName.slice("net.nemerosa.ontrack.extension.".length)

    const importPropertyIcon = () => lazy(() =>
        import(`./${shortTypeName}/Icon`).catch(() => import(`./default/NullIcon`))
    )

    const [loadedPropertyIcon, setLoadedPropertyIcon] = useState(<FaSpinner/>)

    useEffect(() => {
        if (property) {
            const loadPropertyIcon = async () => {
                const LoadedPropertyIcon = await importPropertyIcon()
                setLoadedPropertyIcon(<LoadedPropertyIcon/>)
            }
            loadPropertyIcon().then(() => {
            })
        }
    }, [property])

    const importPropertyDisplay = () => lazy(() =>
        import(`./${shortTypeName}/Display`).catch(() => import(`./default/NullDisplay`))
    )

    const [loadedPropertyDisplay, setLoadedPropertyDisplay] = useState(<FaSpinner/>)

    useEffect(() => {
        if (property) {
            const loadPropertyDisplay = async () => {
                const LoadedPropertyDisplay = await importPropertyDisplay()
                setLoadedPropertyDisplay(<LoadedPropertyDisplay property={property}/>)
            }
            loadPropertyDisplay().then(() => {
            })
        }
    }, [property])

    return (
        <Card style={{width: '100%'}}>
            <Meta
                avatar={loadedPropertyIcon}
                title={property.type.name}
                />
            {loadedPropertyDisplay}
        </Card>
    )
}