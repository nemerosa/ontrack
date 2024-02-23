import {Input} from "antd";
import TableColumnFilterDropdown from "@components/common/TableColumnFilterDropdown";

export default function TableColumnFilterDropdownInput({
                                                           setSelectedKeys,
                                                           selectedKeys,
                                                           confirm,
                                                           clearFilters,
                                                           placeholder,
                                                       }) {
    return (
        <>
            <TableColumnFilterDropdown
                setSelectedKeys={setSelectedKeys}
                selectedKeys={selectedKeys}
                confirm={confirm}
                clearFilters={clearFilters}
            >
                <Input
                    placeholder={placeholder}
                    value={selectedKeys}
                    onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
                    onPressEnter={() => confirm()}
                    style={{width: 188, display: 'block'}}
                />
            </TableColumnFilterDropdown>
        </>
    )
}