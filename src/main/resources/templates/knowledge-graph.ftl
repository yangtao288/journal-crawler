<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>知识图谱关系图</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <style>
        body {
            display: flex;
            flex-direction: column;
            align-items: center;
            font-family: Arial, sans-serif;
        }
        canvas {
            border: 1px solid #ccc;
            margin: 20px;
        }
        button {
            padding: 10px 20px;
            margin: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .legend {
            margin: 20px;
            padding: 10px;
            border: 1px solid #ccc;
        }
        .legend-item {
            margin: 5px;
            display: flex;
            align-items: center;
        }
        .legend-color {
            width: 20px;
            height: 20px;
            margin-right: 10px;
        }
    </style>
</head>
<body>
    <h2>人员关系知识图谱</h2>
    <canvas id="relationshipGraph" width="1000" height="800"></canvas>
    <div class="legend">
        <div class="legend-item">
            <div class="legend-color" style="background-color: #4CAF50;"></div>
            <span>人员节点</span>
        </div>
        <div class="legend-item">
            <div class="legend-color" style="background-color: #2196F3;"></div>
            <span>学校节点</span>
        </div>
        <div class="legend-item">
            <div class="legend-color" style="background-color: #FF9800;"></div>
            <span>项目组节点</span>
        </div>
    </div>
    <button onclick="downloadPDF()">下载PDF</button>

    <script>
        // 从后端获取数据
        const nodes = ${nodes};
        const relationships = ${relationships};

        // 定义节点类型
        const NodeType = {
            PERSON: 'PERSON',
            SCHOOL: 'SCHOOL',
            PROJECT: 'PROJECT'
        };

        const canvas = document.getElementById('relationshipGraph');
        const ctx = canvas.getContext('2d');

        // 根据节点类型获取颜色
        function getNodeColor(type) {
            switch(type) {
                case NodeType.PERSON:
                    return '#4CAF50';
                case NodeType.SCHOOL:
                    return '#2196F3';
                case NodeType.PROJECT:
                    return '#FF9800';
                default:
                    return '#999';
            }
        }

        function drawNode(node) {
            ctx.beginPath();
            const color = getNodeColor(node.type);

            if (node.type === NodeType.PERSON) {
                ctx.arc(node.x, node.y, 25, 0, Math.PI * 2);
            } else {
                ctx.rect(node.x - 40, node.y - 25, 80, 50);
            }

            ctx.fillStyle = color;
            ctx.fill();
            ctx.strokeStyle = '#fff';
            ctx.stroke();

            ctx.fillStyle = 'white';
            ctx.font = '14px Arial';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';

            if (node.name.length > 4) {
                const lines = node.name.match(/.{1,4}/g);
                lines.forEach((line, index) => {
                    const yOffset = (index - (lines.length - 1) / 2) * 20;
                    ctx.fillText(line, node.x, node.y + yOffset);
                });
            } else {
                ctx.fillText(node.name, node.x, node.y);
            }
        }

        function drawGraph() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            relationships.forEach(rel => {
                const source = nodes.find(n => n.id === rel.source);
                const target = nodes.find(n => n.id === rel.target);

                ctx.beginPath();
                ctx.moveTo(source.x, source.y);
                ctx.lineTo(target.x, target.y);
                ctx.strokeStyle = '#999';
                ctx.stroke();

                const textX = (source.x + target.x) / 2;
                const textY = (source.y + target.y) / 2 - 10;
                ctx.fillStyle = '#666';
                ctx.font = '14px Arial';
                ctx.textAlign = 'center';
                ctx.fillText(rel.type, textX, textY);
            });

            nodes.forEach(node => drawNode(node));
        }

        function downloadPDF() {
            const { jsPDF } = window.jspdf;
            const pdf = new jsPDF('l', 'px', [1000, 800]);

            const imageData = canvas.toDataURL('image/png');
            pdf.addImage(imageData, 'PNG', 0, 0, 1000, 800);
            pdf.save('relationship_graph.pdf');
        }

        // 初始绘制
        drawGraph();
    </script>
</body>
</html>