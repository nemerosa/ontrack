import {FloatButton} from "antd";

export default function DependencyLinksModeButton({icon, selectedMode, mode, action, title, disabled}) {

    return (
        <>
            {
                selectedMode && selectedMode !== mode &&
                <FloatButton
                    className="ot-build-links-mode-button"
                    data-testid={`build-links-mode-${mode}`}
                    icon={icon}
                    onClick={() => action(mode)}
                    tooltip={title}
                    disabled={disabled}
                    type="primary"
                />
            }
        </>
    )
}