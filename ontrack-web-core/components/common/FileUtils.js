export const getBase64 = (img, dataUrl, callback) => {
    const reader = new FileReader()
    reader.addEventListener('load', () => {
        const result = reader.result
        if (dataUrl) {
            return callback(result)
        } else {
            const text = btoa(result)
            return callback(text)
        }
    })
    if (dataUrl) {
        reader.readAsDataURL(img)
    } else {
        reader.readAsBinaryString(img)
    }
};

export const formatFileSize = (fileSize) => {
    if (fileSize) {
        if (fileSize < 1024) {
            return `${fileSize} bytes`
        } else {
            return `${Math.floor(fileSize / 1024)} KB`
        }
    } else {
        return ''
    }
}
