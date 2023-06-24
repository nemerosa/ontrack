import {Card} from "antd";
import {FaDocker, FaSpinner} from "react-icons/fa";
import {lazy, useEffect, useState} from "react";

const {Meta} = Card

export default function Property({property}) {
    const shortTypeName = property.type.typeName.slice("net.nemerosa.ontrack.extension.".length)
    console.log({shortTypeName})

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

    return (
        <Card style={{width: '100%'}}>
            <Meta
                avatar={loadedPropertyIcon}
                title={property.type.name}
                />
        </Card>
    )
}