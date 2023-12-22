import {useEffect, useRef} from "react";

export default function GitChangeLogCommitsPlot({plot, onComputedWidth}) {

    const canvasRef = useRef(null)

    const COLORS = [
        'black',
        'red',
        'green',
        'blue'
    ];

    const getColor = item => {
        if (item.color) {
            return COLORS[item.color.index % COLORS.length];
        } else {
            return 'black';
        }
    };

    useEffect(() => {
        const canvas = canvasRef.current
        const context = canvas.getContext('2d')

        console.log({plot})

        const width = plot.width + 2
        canvas.width = width
        canvas.height = plot.height + 2
        if (onComputedWidth) {
            onComputedWidth(width)
        }

        plot.items.forEach(item => {
            drawItem(context, item)
        })

    }, [])

    const drawItem = (context, item) => {
        if (item.type === 'line') {
            drawLine(context, item)
        } else if (item.type === 'oval') {
            drawOval(context, item)
        }
    }

    const drawLine = (context, item) => {
        context.beginPath()
        context.moveTo(item.a.x, item.a.y)
        context.lineTo(item.b.x, item.b.y)
        context.lineWidth = item.width
        context.strokeStyle = getColor(item)
        context.stroke()
    }

    const drawOval = (context, item) => {
        context.beginPath()
        const centerX = item.c.x + item.d.w / 2.0
        const centerY = item.c.y + item.d.h / 2.0
        context.arc(centerX, centerY, item.d.w / 2.0, 0, 2 * Math.PI, false)
        context.fillStyle = getColor(item)
        context.fill()
    }

    return (
        <>
            <canvas ref={canvasRef}/>
        </>
    )
}