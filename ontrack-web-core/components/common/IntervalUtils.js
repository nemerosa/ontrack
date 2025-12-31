export class Interval {
    constructor(value, label, min, max, millisecondsFactor) {
        this.value = value
        this.label = label
        this.min = min
        this.max = max
        this.millisecondsFactor = millisecondsFactor
    }

    displayPeriod(count) {
        return `${count} ${this.label.toLowerCase()}`
    }
}

export const minutes = new Interval(
    'M',
    'Minutes',
    0,
    300,
    60 * 1000,
)

export const units = [
    minutes,
    new Interval('H', 'Hours', 0, 24, 60 * 60 * 1000),
    new Interval('D', 'Days', 0, 365, 24 * 60 * 60 * 1000),
]

export const findInterval = (value) => units.find(unit => unit.value === value)
