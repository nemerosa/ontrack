import {useContext, useEffect, useState} from "react";
import {JobCategoriesContext} from "@components/core/admin/jobs/JobCategoriesContext";
import {Select} from "antd";

export default function SelectJobCategory({value, onChange, style, allowClear}) {
    const {categories, setSelectedCategory} = useContext(JobCategoriesContext)

    const [options, setOptions] = useState([])

    useEffect(() => {
        if (categories) {
            setOptions(
                categories.map(category => ({
                    value: category.key,
                    label: category.name,
                }))
            )
        }
    }, [categories]);

    const onLocalChange = (value) => {
        setSelectedCategory(value)
        if (onChange) onChange(value)
    }

    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onLocalChange}
                allowClear={allowClear}
                style={style}
            />
        </>
    )
}