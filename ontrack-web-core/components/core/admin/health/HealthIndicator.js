import {FaCircle} from "react-icons/fa";

export default function HealthIndicator({status}) {
    return (
        <>
            {
                status === 'UP' && <FaCircle color="green" title="Component or subsystem is functioning as expected"/>
            }
            {
                status !== 'UP' &&
                <FaCircle color="red" title="Component or subsystem has suffered an unexpected failure"/>
            }
        </>
    )
}