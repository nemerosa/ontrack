import {useContext, useEffect, useState} from "react";
import {JobCategoriesContext} from "@components/core/admin/jobs/JobCategoriesContext";
import {Select} from "antd";

export default function SelectJobType({selectedCategory, value, onChange, style, allowClear, placeholder}) {
    const {categories} = useContext(JobCategoriesContext)

    const [options, setOptions] = useState([])

    useEffect(() => {
        if (categories && selectedCategory) {
            const category = categories.find(category => category.key === selectedCategory)
            if (category) {
                setOptions(
                    category.types.map(type => ({
                        value: type.key,
                        label: type.name,
                    }))
                )
            } else {
                setOptions([])
            }
        } else {
            setOptions([])
        }
    }, [categories, selectedCategory]);

    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onChange}
                allowClear={allowClear}
                style={style}
                placeholder={placeholder}
            />
        </>
    )
}