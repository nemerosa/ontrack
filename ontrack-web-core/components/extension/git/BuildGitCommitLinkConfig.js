import {Dynamic} from "@components/common/Dynamic";

export default function BuildGitCommitLinkConfig({prefix, commitLinkId}) {
    return (
        <>
            <Dynamic
                path={`framework/git-commit-link/${commitLinkId}/Form.js`}
                props={{prefix}}
            />
        </>
    )
}