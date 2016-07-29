@Grapes(
    value = [
        @Grab('org.apache.commons:commons-math3:3.6.1'),
        @Grab('org.jfree:jfreechart:1.0.19')
    ]
)
import org.apache.commons.math3.ml.distance.EuclideanDistance
import org.jfree.chart.*
import org.jfree.data.xy.DefaultXYDataset
import org.apache.commons.math3.linear.Array2DRowRealMatrix

def createDataSet() {
    new Tuple2(
        ['A', 'A', 'B', 'B'],
        new Array2DRowRealMatrix([
            [1.0, 1.1],
            [1.0, 1.0],
            [0.0, 0.0],
            [0.0, 0.1]
        ] as double[][])
    )
}

String classify0(inX, dataSet, int k) {
    def distances = []

    dataSet.second.rowDimension.times { row ->
        distances << new Tuple2(
            dataSet.first[row],
            new EuclideanDistance().compute(
                dataSet.second.getRow(row),
                inX as double[]
            )
        )
    }

    distances.sort { it.second }.take(k).countBy { it.first }.max { it.value }.key
}

def fileData(String filename) {
    def groups = []
    def rows = []

    new File(filename).eachLine { String line ->
        def cols = line.trim().split('\t')
        groups << cols[-1]
        rows << (cols[0..(-2)] as double[])
    }

    new Tuple2(groups, new Array2DRowRealMatrix(rows as double[][]))
}

def data = fileData('/home/cjstehno/Desktop/pyml/files/datingTestSet.txt')

def groupIndices = [:]
data.first.eachWithIndex { g, i ->
    if (groupIndices.containsKey(g)) {
        groupIndices[g] << i
    } else {
        groupIndices[g] = [i]
    }
}

def xyDataSet = new DefaultXYDataset()

groupIndices.each { g, indices ->
    def groupMtx = data.second.getSubMatrix(indices as int[], [0, 1, 2] as int[])
    xyDataSet.addSeries(g, [groupMtx.getColumn(1), groupMtx.getColumn(2)] as double[][])
}

def frame = new ChartFrame('Charting', ChartFactory.createScatterPlot(
    'Dating Test',
    'Percentage Time Spent Playing Video Games',
    'Liters of Ice Cream Consumed Per Week',
    xyDataSet
))
frame.setSize(800, 600)
frame.visible = true