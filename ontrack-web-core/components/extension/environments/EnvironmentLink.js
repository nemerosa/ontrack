import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import Link from "next/link";

export default function EnvironmentLink({slot}) {
    return (
        <><Link
            href={slotUri(slot)}
        >
            <EnvironmentIcon
                environmentId={slot.environment.id}
                tooltipText={
                    <>
                        Deployed in {slot.environment.name}
                        {
                            slot.qualifier &&
                            ` [${slot.qualifier}]`
                        }
                    </>
                }
            />
        </Link>
        </>
    )
}