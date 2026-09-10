"use client"

/**
 * A mobile screen's frame: its title, then its content.
 *
 * The title lives in the screen rather than the header because the header is
 * fixed and every row it takes is a row the content does not get - and because a
 * screen knows its own name, while the shell would have to be told.
 */

import {Space, Typography} from "antd"

export default function MobileScreen({title, children}) {
    return (
        <Space direction="vertical" size="middle" style={{width: '100%'}}>
            <Typography.Title
                level={4}
                style={{margin: 0}}
                data-testid="mobile-screen-title"
            >
                {title}
            </Typography.Title>
            {children}
        </Space>
    )
}
