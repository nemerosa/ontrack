import {useRestClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Tooltip} from "antd";

/**
 * This renders an image using a call to the REST API.
 */
export default function ProxyImage({restUri, alt, width, height, onClick, tooltipText}) {

    const client = useRestClient()

    const [dataUrl, setDataUrl] = useState('')

    useEffect(() => {
        if (client && restUri) {
            client.fetch(restUri)
                .then(response => response.arrayBuffer())
                .then(arrayBuffer => {
                    const base64String = btoa(
                        new Uint8Array(arrayBuffer)
                            .reduce((data, byte) => data + String.fromCharCode(byte), '')
                    );

                    // Now you have a Base64-encoded string of your image, convert this to a Data URL
                    setDataUrl(`data:image/png;base64,${base64String}`)
                })
        }
    }, [client, restUri]);

    return (
        <>
            {
                dataUrl &&
                <Tooltip title={tooltipText}>
                    <img
                        src={dataUrl}
                        alt={alt}
                        width={width}
                        height={height}
                        onClick={onClick}
                        className={onClick ? "ot-action" : undefined}
                    />
                </Tooltip>
            }
        </>
    )
}