export const modes = [
    {
        value: 'KEEP',
        label: 'Keep',
    },
    {
        value: 'DISABLE',
        label: 'Disable ',
    },
    {
        value: 'KEEP_LAST',
        label: 'Keep last ',
    },
]

export default function AutoDisablingBranchPatternsMode({mode}) {
    const item = modes.find(it => it.value === mode)
    return item?.label
}