import GeneratedIcon from "@components/common/icons/GeneratedIcon";

export default function EnvironmentIcon({environment}) {
    return (
        <>
            <GeneratedIcon
                name={environment.name}
                colorIndex={environment.order}
            />
        </>
    )
}