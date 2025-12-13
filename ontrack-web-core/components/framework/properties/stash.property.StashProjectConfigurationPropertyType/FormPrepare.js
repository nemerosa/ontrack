export default function FormPrepare(value) {
    return {
        ...value,
        configuration: value?.configuration?.name,
    }
}
