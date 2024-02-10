export const generate = (prefix) => {
    const root = Math.floor(Math.random() * 100000)
    return `${prefix}${root}`
}

export function trimIndent(str) {
    // Split the string into an array of lines
    const lines = str.split('\n')

    // Find the minimum indent among non-empty lines
    let minIndent = Infinity;
    for (let line of lines) {
        if (line.trim() !== '') {
            const indent = line.match(/^\s*/)[0].length
            minIndent = Math.min(minIndent, indent)
        }
    }

    // Remove the minimum indent from each line
    const trimmedLines = lines.map(line => line.substring(minIndent))

    // Join the lines back into a single string
    return trimmedLines.join('\n')
}