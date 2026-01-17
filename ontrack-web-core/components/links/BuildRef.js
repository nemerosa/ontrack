import BuildLink from "@components/builds/BuildLink";

export default function BuildRef({build, displayTooltip = true, tooltipText}) {
    return (
        <>
            <BuildLink
                build={build}
                displayTooltip={displayTooltip}
                tooltipText={tooltipText}
            />
        </>
    )
}