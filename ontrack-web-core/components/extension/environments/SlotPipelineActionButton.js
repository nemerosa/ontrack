import {Button, Popconfirm, Space} from "antd";
import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import LoadingInline from "@components/common/LoadingInline";
import {useEffect, useState} from "react";

export default function SlotPipelineActionButton({
                                                     id,
                                                     status,
                                                     actionStateData,
                                                     actionStateLoading,
                                                     confirmTitle,
                                                     confirmDescription,
                                                     buttonTitle,
                                                     buttonText,
                                                     action,
                                                     actionRunning,
                                                     size,
                                                     showDisabledButtonIfNotOk = false,
                                                     showIcon = true,
                                                     showText = false,
                                                 }) {

    const [display, setDisplay] = useState(false)
    const [disabled, setDisabled] = useState(true)
    useEffect(() => {
        if (actionStateData) {
            if (actionStateData.ok) {
                setDisplay(true)
                setDisabled(false)
            } else if (showDisabledButtonIfNotOk) {
                setDisplay(true)
                setDisabled(true)
            } else {
                setDisplay(false)
                setDisabled(true)
            }
        } else {
            setDisplay(false)
            setDisabled(true)
        }
    }, [actionStateData, showDisabledButtonIfNotOk])

    return (
        <>
            {
                <LoadingInline loading={actionStateLoading} text="">
                    <Space>
                        {
                            display &&
                            <Popconfirm
                                title={confirmTitle}
                                description={confirmDescription}
                                onConfirm={action}
                            >
                                <Button
                                    title={buttonTitle}
                                    loading={actionRunning}
                                    data-testid={id}
                                    size={size}
                                    disabled={disabled}
                                >
                                    <Space>
                                        {
                                            showIcon &&
                                            <SlotPipelineStatusIcon status={status}/>
                                        }
                                        {
                                            showText && buttonText
                                        }
                                    </Space>
                                </Button>
                            </Popconfirm>
                        }
                    </Space>
                </LoadingInline>
            }
        </>
    )
}