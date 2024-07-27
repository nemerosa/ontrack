export const formFieldArraySwap = (form, fieldName, oldIndex, newIndex) => {
    const array = form.getFieldValue(fieldName)
    const oldElement = array[oldIndex]
    array[oldIndex] = array[newIndex]
    array[newIndex] = oldElement
    form.setFieldValue(fieldName, array)
}

export const prefixedFormName = (prefix, name) => {
    let values
    if (typeof name === 'string') {
        values = [name]
    } else {
        values = [...name]
    }
    if (typeof prefix === 'string') {
        return [prefix, ...values]
    } else {
        return [...prefix, ...values]
    }
}
