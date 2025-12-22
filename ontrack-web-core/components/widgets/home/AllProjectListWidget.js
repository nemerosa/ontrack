import {useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {gqlDecorationFragment} from "@components/services/fragments";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import PaddedContent from "@components/common/PaddedContent";
import {gqlProjectContentFragment} from "@components/projects/ProjectGraphQLFragments";
import {useQuery} from "@components/services/GraphQL";
import SimpleProjectList from "@components/projects/SimpleProjectList";
import {Button, Form, Input} from "antd";
import {FaBackwardStep, FaForwardStep} from "react-icons/fa6";

export default function AllProjectListWidget() {


    const [pagination, setPagination] = useState({
        offset: 0,
        size: 20,
    })

    const [pageInfo, setPageInfo] = useState({
        previousPage: null,
        nextPage: null,
    })

    const [filter, setFilter] = useState({
        projectName: null,
    })

    const {data, loading} = useQuery(
        gql`
            query AllProjectListWidget($offset: Int! = 0, $size: Int! = 20, $name: String = null) {
                paginatedProjects(offset: $offset, size: $size, name: $name) {
                    pageInfo {
                        previousPage {
                            offset
                            size
                        }
                        nextPage {
                            offset
                            size
                        }
                    }
                    pageItems {
                        ...ProjectContent
                        favourite
                        decorations {
                            ...decorationContent
                        }
                    }
                }
            }
            ${gqlDecorationFragment}
            ${gqlProjectContentFragment}
        `,
        {
            variables: {
                ...pagination,
                name: filter.projectName,
            },
            deps: [pagination, filter],
            initialData: {pageItems: []},
            dataFn: data => data.paginatedProjects,
        }
    )

    useEffect(() => {
        setPageInfo(data.pageInfo)
    }, [data])

    const onFinish = values => {
        setPagination({
            offset: 0,
            size: 20,
        })
        setFilter(values)
    }

    const onClear = () => {
        onFinish({projectName: null})
    }

    const [form] = Form.useForm()

    const projectNameInput = <Form layout="inline" form={form} onFinish={onFinish}>
        <Form.Item name="projectName">
            <Input placeholder="Project name" allowClear onClear={onClear}/>
        </Form.Item>
    </Form>

    const {setTitle, setExtra} = useContext(DashboardWidgetCellContext)
    useEffect(() => {
        setTitle("All projects")
        setExtra(projectNameInput)
    }, [])

    const onPrevious = () => {
        if (pageInfo.previousPage) {
            setPagination(pageInfo.previousPage)
        }
    }

    const onNext = () => {
        if (pageInfo.nextPage) {
            setPagination(pageInfo.nextPage)
        }
    }

    return (
        <PaddedContent>
            <SimpleProjectList
                projects={data.pageItems}
                emptyText={
                    <>
                        No project has been created in Ontrack yet.
                        You can start <a
                        href="https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#feeding">feeding
                        information</a> in Ontrack
                        automatically from your CI engine, using its API or other means.
                    </>
                }
                before={
                    pageInfo && pageInfo.previousPage &&
                    <Button onClick={onPrevious} type="link" loading={loading}
                            icon={<FaBackwardStep/>}>Previous</Button>
                }
                after={
                    pageInfo && pageInfo.nextPage &&
                    <Button onClick={onNext} type="link" loading={loading} icon={<FaForwardStep/>}>Next</Button>
                }
            />
        </PaddedContent>
    )
}