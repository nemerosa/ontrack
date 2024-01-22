import BuildLink from "@components/builds/BuildLink";

export default function BuildRef({build}) {
    return (
        <>
            <BuildLink
                build={build}
                displayTooltip={true}
            />
        </>
    )
}