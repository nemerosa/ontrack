import {FloatButton} from "antd";

export default function BranchLinksModeButton({icon, mode, href, onClick, title}) {
    return (
        <>
            {
                <FloatButton
                    className="ot-branch-links-mode-button"
                    data-testid={`branch-links-mode-${mode}`}
                    icon={icon}
                    href={href}
                    onClick={onClick}
                    tooltip={title}
                    type="primary"
                />
            }
        </>
    )
}