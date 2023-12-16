export const formFieldArraySwap = (form, fieldName, oldIndex, newIndex) => {
    const array = form.getFieldValue(fieldName)
    const oldElement = array[oldIndex]
    array[oldIndex] = array[newIndex]
    array[newIndex] = oldElement
    form.setFieldValue(fieldName, array)
}
