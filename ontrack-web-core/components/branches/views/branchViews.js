import {FaBars, FaVial} from "react-icons/fa";
import ClassicBranchView from "@components/branches/views/ClassicBranchView";
import TestBranchView from "@components/branches/views/TestBranchView";

export function getBranchViews(branch) {
    return [
        {
            key: 'classic',
            label: "Classic view",
            icon: <FaBars/>,
            component: <ClassicBranchView branch={branch}/>,
        },
        {
            key: 'test',
            label: "Test view",
            icon: <FaVial/>,
            component: <TestBranchView branch={branch}/>,
        },
    ]
}
