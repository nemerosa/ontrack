export default function prepare(value) {
    return {
        ...value,
        validationStamps: value.validationStamps?.map(vs => vs.id) ?? [],
        promotionLevels: value.promotionLevels?.map(pl => pl.id) ?? [],
    }
}