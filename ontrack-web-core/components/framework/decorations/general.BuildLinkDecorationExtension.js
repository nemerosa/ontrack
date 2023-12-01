import {buildLink} from "@components/common/Links";
import {FaLink} from "react-icons/fa";
import {Tag} from "antd";
import Link from "next/link";

export default function BuildLinkDecorationExtension({decoration}) {
    return (
        <>
            {
                decoration.data.buildId &&
                decoration.data.linksCount &&
                decoration.data.linksCount > 0 &&
                <Tag title={
                    `This build has ${decoration.data.linksCount} downstream link(s).`
                }>
                    <Link href={buildLink({id: decoration.data.buildId})}>
                        <FaLink size={8}/>
                    </Link>
                </Tag>
            }
        </>
    )
}