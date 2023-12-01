import {useConnection} from "@components/providers/ConnectionContextProvider";

export default function LegacyImage({href, alt, width, height}) {
    const {environment} = useConnection()
    return (
        <>
            <img
                src={`${environment.ontrack.url}${href}`}
                alt={alt}
                width={width}
                height={height}
            />
        </>
    )
}