import {FloatButton} from "antd";

export default function DependencyLinksModeButton({icon, selectedMode, mode, action, title, disabled}) {

    return (
        <>
            {
                selectedMode && selectedMode !== mode &&
                <FloatButton
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