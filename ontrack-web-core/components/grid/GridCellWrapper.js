import React from "react";

// See https://github.com/react-grid-layout/react-grid-layout#custom-child-components-and-draggable-handles
export const GridCellWrapper = React.forwardRef(({
                                                     style,
                                                     className,
                                                     onMouseDown,
                                                     onMouseUp,
                                                     onTouchEnd,
                                                     children,
                                                     ...props
                                                 }, ref) => {
    return (
        <div style={{...style}}
             className={className}
             ref={ref}
             onMouseDown={onMouseDown}
             onMouseUp={onMouseUp}
             onTouchEnd={onTouchEnd}
        >
            {children}
        </div>
    )
})

GridCellWrapper.displayName = "GridCellWrapper"
