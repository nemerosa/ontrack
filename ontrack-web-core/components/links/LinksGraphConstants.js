import {MarkerType} from "reactflow";

const edgeColour = '#999'

export const edgeStyle = {
    type: 'smoothstep',
    style: {
        stroke: edgeColour,
        strokeWidth: 2,
    },
    markerEnd: {
        type: MarkerType.ArrowClosed,
        width: 20,
        height: 20,
        color: edgeColour,
    },
}