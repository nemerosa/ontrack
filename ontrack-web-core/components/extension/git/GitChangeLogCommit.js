import {Space, Typography} from "antd";
import Link from "next/link";
import SafeHTMLComponent from "@components/common/SafeHTMLComponent";
import TimestampText from "@components/common/TimestampText";
import {buildUri} from "@components/common/Links";
import {buildKnownName, buildLinkName} from "@components/common/Titles";
import PromotionRun from "@components/promotionRuns/PromotionRun";

export default function GitChangeLogCommit({commit}) {
    return (
        <div className="ot-git-plot-line" id={`commit-${commit.commit.id}`}>
            <Space size={8}>
                {/* Commit short hash */}
                <Link href={commit.commit.link} title="Link to the commit in the SCM">
                    <Typography.Text code>{commit.commit.shortId}</Typography.Text>
                </Link>
                {/* Annotated message */}
                <SafeHTMLComponent htmlContent={commit.annotatedMessage}/>
                {/* Author */}
                <span className="ot-git-plot-line-author" title="Author for the commit">{commit.commit.author}</span>
                {/* Timestamp */}
                <span className="ot-git-plot-line-timestamp" title="Timestamp for the commit"><TimestampText value={commit.commit.timestamp}/></span>
                {/* Build decoration */}
                {
                    commit.build &&
                    <Link href={buildUri(commit.build)}>
                        <span className="ot-git-plot-line-build">{buildKnownName(commit.build)}</span>
                    </Link>
                }
                {/* Build promotions */}
                {
                    commit.build && commit.build.promotionRuns &&
                    <Space size={8}>
                        {
                            commit.build.promotionRuns.map(promotionRun =>
                                <PromotionRun
                                    key={promotionRun.id}
                                    promotionRun={promotionRun}
                                    size={16}
                                />
                            )
                        }
                    </Space>
                }
                {/* Build links */}
                {
                    commit.build && commit.build.usingQualified.pageItems &&
                    <Space size={8}>
                        {
                            commit.build.usingQualified.pageItems.map(link =>
                                <Link key={`${link.build.id}-${link.qualifier}`} href={buildUri(link.build)}>
                                    <span className="ot-git-plot-line-build">
                                        {buildLinkName(link)}
                                    </span>
                                </Link>
                            )
                        }
                    </Space>
                }
            </Space>
        </div>
    )
}