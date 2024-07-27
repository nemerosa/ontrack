export default function JenkinsBuildPropertyTypeFormPrepare(value) {
    return {
        ...value,
        configuration: value?.configuration?.name,
    }
}
