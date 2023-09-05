import {Card, Space, Spin, Typography} from "antd";
import {FaSpinner} from "react-icons/fa";
import {lazy, useEffect, useState} from "react";
import ErrorBoundary from "@components/common/ErrorBoundary";
import Section from "@components/common/Section";

const {Meta} = Card

export default function Property({property}) {

    const shortTypeName = property.type.typeName.slice("net.nemerosa.ontrack.extension.".length)

    const importPropertyIcon = () => lazy(() =>
        import(`./${shortTypeName}/Icon`).catch(() => {
            console.error(`Cannot find icon for property ${JSON.stringify(property)}: ${shortTypeName}`)
            return import(`./default/NullIcon`);
        })
    )

    const [loadedPropertyIcon, setLoadedPropertyIcon] = useState(<Spin size="small"/>)

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
        import(`./${shortTypeName}/Display`).catch(() => {
            console.error(`Cannot find display for property ${JSON.stringify(property)}: ${shortTypeName}`)
            return import(`./default/NullDisplay`);
        })
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
        <ErrorBoundary fallback={<p>Cannot display property</p>}>
            <Section title={
                <Space>
                    {loadedPropertyIcon}
                    <Typography.Text strong>{property.type.name}</Typography.Text>
                </Space>
            }>
                {loadedPropertyDisplay}
            </Section>
        </ErrorBoundary>
    )
}