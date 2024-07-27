export default function JenkinsJobPropertyTypeFormPrepare(value) {
    return {
        ...value,
        configuration: value?.configuration?.name,
    }
}
