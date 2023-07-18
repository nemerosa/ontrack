import {lazy, useEffect, useState} from "react";
import {FaSpinner} from "react-icons/fa";

export default function Decoration({decoration}) {
    const shortTypeName = decoration.decorationType.slice("net.nemerosa.ontrack.extension.".length)

    const importDecorationDisplay = () => lazy(() =>
        import(`./${shortTypeName}`).catch(() => {
            console.warn(`Undefined decoration type: ${shortTypeName}`)
            return import(`./default`);
        })
    )

    const [loadedDecorationDisplay, setLoadedDecorationDisplay] = useState(<FaSpinner/>)

    useEffect(() => {
        if (decoration) {
            const loadDecoration = async () => {
                const LoadedDecorationDisplay = await importDecorationDisplay()
                setLoadedDecorationDisplay(<LoadedDecorationDisplay decoration={decoration}/>)
            }
            loadDecoration().then(() => {
            })
        }
    }, [decoration])

    return (
        <>
            {loadedDecorationDisplay}
        </>
    )
}