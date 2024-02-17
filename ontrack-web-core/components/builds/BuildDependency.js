import {Space, Typography} from "antd";
import {FaLink} from "react-icons/fa";
import BuildBox from "@components/builds/BuildBox";
import PromotionRun from "@components/promotionRuns/PromotionRun";

export default function BuildDependency({
                                            link,
                                            displayProject = true,
                                            displayPromotions,
                                            displayBox = false,
                                            displayIcon = false,
                                            displayDecorations = true,
                                        }) {
    return (
        <BuildBox
            displayDecorations={displayDecorations}
            className={displayBox ? "ot-dependency" : undefined}
            build={link.build}
            text={
                <Space>
                    {displayIcon ? <FaLink/> : undefined}
                    {
                        displayProject ?
                            `${link.build.name} @ ${link.build.branch.project.name}` :
                            link.build.name
                    }
                    {link.qualifier && <Typography.Text>[{link.qualifier}]</Typography.Text>}
                </Space>
            }
            extra={
                displayPromotions && link.build.promotionRuns && <Space size={8}>
                    {
                        link.build.promotionRuns.map(promotionRun =>
                            <PromotionRun
                                key={promotionRun.id}
                                promotionRun={promotionRun}
                                size={16}
                            />
                        )
                    }
                </Space>
            }
            creationDisplayMode="tooltip"
        />
    )
}