import {Button, Card, Form, Space} from "antd";
import {FaBan, FaFilter} from "react-icons/fa";

export default function FilterForm({setFilterFormData, filterForm, filterExtraButtons}) {

    const [filterFormInstance] = Form.useForm()

    const onFilterFormFinish = (values) => {
        setFilterFormData(values)
    }

    const onFilterFormClear = () => {
        filterFormInstance.resetFields()
        setFilterFormData({})
    }

    return (
        <>
            <Card
                size="small"
                className="ot-well"
            >
                <Form
                    layout="inline"
                    onFinish={onFilterFormFinish}
                    form={filterFormInstance}
                    style={{
                        rowGap: 16,
                        columnGap: 8,
                    }}
                >
                    {filterForm}
                    {/* Filter */}
                    <Button
                        type="primary"
                        htmlType="submit"
                    >
                        <Space>
                            <FaFilter/>
                            Filter
                        </Space>
                    </Button>
                    {/* Clear */}
                    <Button
                        type="link"
                        onClick={onFilterFormClear}
                    >
                        <Space>
                            <FaBan/>
                            Clear filter
                        </Space>
                    </Button>
                    {/* Extra buttons */}
                    {filterExtraButtons}
                </Form>
            </Card>
        </>
    )
}