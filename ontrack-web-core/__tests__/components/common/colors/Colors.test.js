import {getTextColorForBackground, numberToColorHsl} from "@components/common/colors/Colors";

describe('Colors', () => {
    it('generate colors', () => {
        const inputNumber = 12345
        const bgColor = numberToColorHsl(inputNumber)
        const textColor = getTextColorForBackground(bgColor)

        expect(bgColor).toEqual("#66cc7d")
        expect(textColor).toEqual("#000000")
    })
})