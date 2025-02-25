import {Popover, Space, Table, Typography} from "antd";
import React from "react";
import ValidationStamp from "@components/validationStamps/ValidationStamp";
import ValidationRunLink from "@components/validationRuns/ValidationRunLink";
import ValidationRunStatus from "@components/validationRuns/ValidationRunStatus";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import {FaInfoCircle} from "react-icons/fa";
import Timestamp from "@components/common/Timestamp";
import RunInfo from "@components/common/RunInfo";
import ValidationRunData from "@components/framework/validation-run-data/ValidationRunData";

export default function ValidationRunTable({validationRuns, pagination = false, onChange, filtering}) {

    // Definition of the columns

    const columns = [
        {
            title: "Validation",
            key: 'validation',
            render: (_, run) => <ValidationStamp validationStamp={run.validationStamp} tooltipPlacement="rightBottom"/>,
            filters: filtering?.validationStamps,
            filterSearch: true,
            filterMultiple: false,
            filteredValue: filtering?.filteredInfo?.validation || null,
        },
        {
            title: "Run",
            key: 'run',
            render: (_, run) => <ValidationRunLink run={run}/>
        },
        {
            title: "Status",
            key: 'status',
            render: (_, run) => <ValidationRunStatus status={run.lastStatus}/>,
            filters: filtering?.statuses,
            filterSearch: true,
            filteredValue: filtering?.filteredInfo?.status || null,
        },
        {
            title: "Description",
            key: 'description',
            render: (_, run) => <AnnotatedDescription entity={run.lastStatus}/>,
        },
        {
            title: "Creation",
            key: 'creation',
            render: (_, run) => <Popover
                content={
                    <Space direction="vertical">
                        <Typography.Text>Created by {run.lastStatus.creation.user}</Typography.Text>
                        <AnnotatedDescription entity={run.lastStatus}/>
                    </Space>
                }
            >
                <Space>
                    <FaInfoCircle/>
                    <Timestamp value={run.lastStatus.creation.time} fontSize="100%"></Timestamp>
                </Space>
            </Popover>
        },
        {
            title: "Run info",
            key: 'run-info',
            render: (_, run) => run.runInfo ? <RunInfo info={run.runInfo} mode="minimal"/> : undefined
        },
        {
            title: "Data",
            key: 'data',
            render: (_, run) => <ValidationRunData data={run.data}/>
        }
    ]

    return (
        <>
            <Table
                dataSource={validationRuns}
                columns={columns}
                pagination={pagination}
                onChange={onChange}
                size="small"
            />
        </>
    )
}