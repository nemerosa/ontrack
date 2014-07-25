angular.module('ot.service.plot', [
])
    .service('otPlot', function ($log) {
        var self = {};

        var COLORS = [
            'black',
            'red',
            'green',
            'blue'
        ];

        function getColor(item) {
            if (item.color) {
                return COLORS[item.color.index % COLORS.length];
            } else {
                return 'black';
            }
        }

        function drawLine(context, item) {
            context.beginPath();
            context.moveTo(item.a.x, item.a.y);
            context.lineTo(item.b.x, item.b.y);
            context.lineWidth = item.width;
            context.strokeStyle = getColor(item);
            context.stroke();
        }

        function drawOval(context, item) {
            context.beginPath();
            var centerX = item.c.x + item.d.w / 2.0;
            var centerY = item.c.y + item.d.h / 2.0;
            context.arc(centerX, centerY, item.d.w / 2.0, 0, 2 * Math.PI, false);
            context.fillStyle = getColor(item);
            context.fill();
        }

        function drawItem(ctx, item) {
            if ('line' == item.type) {
                drawLine(ctx, item);
            } else if ('oval' == item.type) {
                drawOval(ctx, item);
            } else {
                $log.error('[plot] Unknown item type: ', item.type);
            }
        }

        function draw(canvas, plot) {
            // Size
            canvas.width = plot.width + 2;
            canvas.height = plot.height + 2;
            // Context
            var ctx = canvas.getContext('2d');
            // All items
            $.each(plot.items, function (index, item) {
                drawItem(ctx, item);
            });
        }

        self.draw = draw;

        return self;
    })
;