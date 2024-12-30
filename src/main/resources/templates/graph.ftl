<!DOCTYPE html>
<html>
<head>
    <title>知识关系图谱</title>

    <script src="/js/vis.min.js"></script>
    <script src="/js/jspdf.umd.min.js"></script>
    <link rel="stylesheet" href="/js/vis.min.css">

    <style>
        #mynetwork {
            width: 1000px;
            height: 800px;
            border: 1px solid lightgray;
            background-color: #ffffff;
        }
        .controls {
            margin: 10px;
        }
        .vis-network {
            outline: none;
        }
    </style>
</head>
<body>
    <div class="controls">
        <button onclick="downloadPDF()">下载PDF</button>
    </div>
    <div id="mynetwork"></div>

    <script>
        // 处理边的数据，为不同类型的关系设置不同的箭头样式
        const edgesData = ${edgesJson};
        const processedEdges = edgesData.map(edge => {
            const noArrowRelations = ['同事', '合作', '师生', '同学'];
            return {
                ...edge,
                arrows: noArrowRelations.includes(edge.label) ? {} : { to: { enabled: true, scaleFactor: 1 } }
            };
        });

        const nodes = new vis.DataSet(${nodesJson});
        const edges = new vis.DataSet(processedEdges);

        const options = {
            nodes: {
                shape: 'box',
                margin: 10,
                widthConstraint: {
                    minimum: 100,
                    maximum: 150
                },
                font: {
                    size: 16,
                    face: 'Arial',
                    color: '#000000',
                    bold: true
                },
                borderWidth: 2,
                shadow: {
                    enabled: true,
                    color: 'rgba(0,0,0,0.2)',
                    size: 10,
                    x: 5,
                    y: 5
                }
            },
            edges: {
                width: 2,
                color: {
                    color: '#848484',
                    highlight: '#848484',
                    hover: '#848484'
                },
                font: {
                    size: 14,
                    face: 'Arial',
                    color: '#343434',
                    align: 'horizontal',
                    background: 'white'
                },
                shadow: {
                    enabled: true,
                    color: 'rgba(0,0,0,0.1)',
                    size: 10,
                    x: 5,
                    y: 5
                }
            },
            groups: {
                person: {
                    color: {
                        background: '#E8F4FF',
                        border: '#2B7CE9'
                    }
                },
                school: {
                    color: {
                        background: '#FFE8E8',
                        border: '#FA0010'
                    }
                },
                project: {
                    color: {
                        background: '#E8FFE8',
                        border: '#4C9A2A'
                    }
                }
            },
            physics: {
                enabled: true,
                stabilization: {
                    enabled: true,
                    iterations: 1000,
                    updateInterval: 100
                },
                barnesHut: {
                    gravitationalConstant: -20000,
                    springConstant: 0.04,
                    springLength: 200,
                    damping: 0.09
                }
            },
            layout: {
                randomSeed: 2,
                improvedLayout: true
            }
        };

        // 创建网络图
        const container = document.getElementById('mynetwork');
        const data = {
            nodes: nodes,
            edges: edges
        };
        const network = new vis.Network(container, data, options);

        // 保存节点位置
        network.on("dragEnd", function(params) {
            if (params.nodes.length > 0) {
                const nodeId = params.nodes[0];
                const position = network.getPositions([nodeId])[nodeId];
                nodes.update({
                    id: nodeId,
                    x: position.x,
                    y: position.y,
                    fixed: true
                });
            }
        });

        // 优化节点位置
        network.once('stabilizationIterationsDone', function() {
            const positions = network.getPositions();
            Object.keys(positions).forEach(nodeId => {
                nodes.update({
                    id: nodeId,
                    x: positions[nodeId].x,
                    y: positions[nodeId].y
                });
            });
        });

        function downloadPDF() {
            const { jsPDF } = window.jspdf;
            const pdf = new jsPDF();

            const canvas = document.querySelector('canvas');
            const imgData = canvas.toDataURL('image/jpeg', 1.0);

            pdf.addImage(imgData, 'JPEG', 10, 10, 190, 140);
            pdf.save('knowledge-graph.pdf');
        }
    </script>
</body>
</html>