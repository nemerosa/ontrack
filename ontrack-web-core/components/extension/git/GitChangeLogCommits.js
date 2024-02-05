import GridCell from "@components/grid/GridCell";
import {Col, Row} from "antd";
import GitChangeLogCommit from "@components/extension/git/GitChangeLogCommit";
import GitChangeLogCommitsPlot from "@components/extension/git/GitChangeLogCommitsPlot";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import Link from "next/link";

export default function GitChangeLogCommits({id, commits, diffLink}) {

    const [graphWidth, setGraphWidth] = useState(32)

    const onComputedWidth = (width) => {
        setGraphWidth(width)
    }

    return (
        <>
            <GridCell
                id={id}
                title={
                    <>
                        Commits (<Link href={diffLink}>diff</Link>)
                    </>
                }
            >
                <Row>
                    {/*<Col style={{width: graphWidth}}>*/}
                    {/*    {*/}
                    {/*        changeLog.commitsPlot &&*/}
                    {/*        <GitChangeLogCommitsPlot*/}
                    {/*            plot={changeLog.commitsPlot}*/}
                    {/*            onComputedWidth={onComputedWidth}*/}
                    {/*        />*/}
                    {/*    }*/}
                    {/*</Col>*/}
                    <Col>
                        {
                            commits &&
                            commits.map(commit =>
                                <GitChangeLogCommit
                                    key={commit.commit.id}
                                    commit={commit}
                                />
                            )
                        }
                    </Col>
                </Row>
            </GridCell>
        </>
    )
}
