import {buildDependencyDownstreamGraph} from "@components/common/Links";
import {FaLink} from "react-icons/fa";
import {Tag} from "antd";
import LegacyLink from "@components/common/LegacyLink";

export default function BuildLinkDecorationExtension({decoration}) {
    return (
        <>
            {
                decoration.data.buildId &&
                decoration.data.linksCount &&
                decoration.data.linksCount > 0 &&
                <Tag title={
                    `This build has ${decoration.data.linksCount} downstream link(s). Click on this link to check its dependency graph.`
                }>
                    <LegacyLink href={buildDependencyDownstreamGraph({id: decoration.data.buildId})}>
                        <FaLink size={8}/>
                    </LegacyLink>
                </Tag>
            }
        </>
    )
}