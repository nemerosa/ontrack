export const slotName = (slot) =>
    `${slot.environment.name}/${slot.project.name}${slot.qualifier ? ` [${slot.qualifier}]` : ''}`

export const slotNameWithoutProject = (slot) =>
    `${slot.environment.name}${slot.qualifier ? ` [${slot.qualifier}]` : ''}`
