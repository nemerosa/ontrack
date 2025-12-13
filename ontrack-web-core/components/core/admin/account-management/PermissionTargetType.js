import {FaUser, FaUsers} from "react-icons/fa";

export default function PermissionTargetType({type}) {
    if (type === 'ACCOUNT') {
        return <FaUser title="Account"/>
    } else if (type === 'GROUP') {
        return <FaUsers title="Account group"/>
    } else {
        return null
    }
}