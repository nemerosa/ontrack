import RowTag from "@components/common/RowTag";
import ProjectBox from "@components/projects/ProjectBox";
import {Col, Row} from "antd";
import BranchLastPromotionBox from "@components/branches/BranchLastPromotionBox";

export default function ProjectRow({project}) {

    return (
        <>
            <Row gutter={[8, 8]}>
                <Col flex="none">
                    <RowTag><ProjectBox project={project}/></RowTag>
                </Col>
                {
                    project.branches && project.branches.map(branch =>
                        <Col key={branch.id}>
                            <RowTag>
                                <BranchLastPromotionBox branch={branch}/>
                            </RowTag>
                        </Col>
                    )
                }
            </Row>

        </>
    )
}