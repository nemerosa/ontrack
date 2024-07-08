export default function Result({data}) {
    return {
        title: data.item.commitShort,
        description: data.item.commitMessage,
    }
}