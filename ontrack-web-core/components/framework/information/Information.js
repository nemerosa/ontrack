import {Card, Spin} from "antd";
import {lazy, useEffect, useState} from "react";
import ErrorBoundary from "@components/common/ErrorBoundary";

const {Meta} = Card

export default function Information({info}) {

    const shortTypeName = info.type.slice("net.nemerosa.ontrack.extension.".length)

    const importInfoDisplay = () => lazy(() =>
        import(`./${shortTypeName}`).catch(() => {
            console.warn(`Undefined information type: ${shortTypeName}`)
            return import(`./default.js`);
        })
    )

    const [loadedInfoDisplay, setLoadedInfoDisplay] = useState(<Spin size="small"/>)

    useEffect(() => {
        if (info) {
            const loadInfo = async () => {
                const LoadedInfoDisplay = await importInfoDisplay()
                setLoadedInfoDisplay(<LoadedInfoDisplay info={info}/>)
            }
            loadInfo().then(() => {
            })
        }
    }, [info])

    return (
        <ErrorBoundary fallback={<p>Cannot display property</p>}>
            {loadedInfoDisplay}
        </ErrorBoundary>
    )
}