import {useEffect, useState} from "react";
import {Tooltip} from "antd";
import {actionClassName} from "@components/common/ClassUtils";

/**
 * This renders an image using a call to the REST API.
 */
export default function ProxyImage({restUri, alt, width, height, onClick, tooltipText, disabled = false}) {

    const [dataUrl, setDataUrl] = useState('')

    useEffect(() => {
        if (restUri) {
            fetch(restUri, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                }
            }).then(res => {
                if (res.ok) {
                    return res.json()
                } else {
                    console.error(res)
                    throw new Error(`Issue with proxy image at ${restUri}`);
                }
            }).then(({dataURL}) => {
                setDataUrl(dataURL)
            })
        }
    }, [restUri])

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
                        className={actionClassName(onClick, disabled)}
                    />
                </Tooltip>
            }
        </>
    )
}