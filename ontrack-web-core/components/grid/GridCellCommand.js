import {Button, Tooltip} from "antd";
import Link from "next/link";

export default function GridCellCommand({
                                            condition = true,
                                            disabled = false,
                                            title,
                                            icon,
                                            onAction,
                                            href,
                                            className,
                                            children
                                        }) {
    return (
        <>
            {
                condition &&
                <Tooltip title={title}>
                    <div>
                        <Button
                            disabled={disabled}
                            className={className}
                            icon={!href && icon}
                            onClick={onAction}
                        >
                            {
                                href && <Link href={href}>{icon}</Link>
                            }
                            {
                                children
                            }
                        </Button>
                    </div>
                </Tooltip>
            }
        </>
    )
}