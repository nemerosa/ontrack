import {Popover, Space} from "antd";
import {buildLink} from "@components/common/Links";
import Decorations from "@components/framework/decorations/Decorations";
import Timestamp from "@components/common/Timestamp";

/**
 *
 * @param build
 * @param text Alternative text for the build (build name by default)
 * @param extra Components to add at the right of the box
 * @param creationDisplayMode How the build timestamp information is displayed:
 *   * inline (default) - just below the build links & decorations
 *   * tooltip - as a tooltip
 * @returns {JSX.Element}
 * @constructor
 */
export default function BuildBox({build, text, extra, creationDisplayMode = "inline", className}) {

    const core =
        <Space direction="vertical">
            <Space>
                {buildLink(build, text)}
            </Space>
            {
                creationDisplayMode === 'inline' &&
                <Timestamp prefix="Created on" value={build.creation.time}/>
            }
        </Space>

    return (
        <>
            <Space className={className}>
                {
                    creationDisplayMode === 'tooltip' &&
                    <Popover
                        content={<Timestamp prefix="Created on" value={build.creation.time}/>}
                    >
                        {core}
                    </Popover>
                }
                <Decorations entity={build}/>
                {
                    creationDisplayMode !== 'tooltip' && core
                }
                {extra}
            </Space>
        </>
    )
}