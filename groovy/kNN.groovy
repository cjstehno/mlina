@Grapes(
    value = [
        @Grab('org.apache.commons:commons-math3:3.6.1'),
        @Grab('org.jfree:jfreechart:1.0.19')
    ]
)
import org.apache.commons.math3.ml.distance.EuclideanDistance
import org.jfree.chart.*
import org.jfree.data.xy.DefaultXYDataset

def createDataSet() {
    [
        new Tuple2('A', [1.0, 1.1]),
        new Tuple2('A', [1.0, 1.0]),
        new Tuple2('B', [0.0, 0.0]),
        new Tuple2('B', [0.0, 0.1])
    ]
}

String classify0(inX, dataSet, int k) {
    dataSet.collect { d ->
        new Tuple2<>(
            d.first, new EuclideanDistance().compute(d.second as double[], inX as double[])
        )
    }.sort { it.second }.take(k).countBy { it.first }.max { it.value }.key
}

def fileData(String filename) {
    def data = []
    new File(filename).eachLine { String line ->
        def cols = line.trim().split('\t')
        data << new Tuple2(cols[-1], cols[0..(-2)])
    }
    data
}

def data = fileData('/home/cjstehno/Desktop/pyml/files/datingTestSet.txt')

def xyDataSet = new DefaultXYDataset()

data.groupBy { it.first }.each { g,dat->
    xyDataSet.addSeries(
        g,
        [
            dat.collect { d -> d.second[1] } as double[],
            dat.collect { d -> d.second[2] } as double[]
        ] as double[][]
    )
}

def frame = new ChartFrame('Charting', ChartFactory.createScatterPlot(
    'Dating Test',
    'Percentage Time Spent Playing Video Games',
    'Liters of Ice Cream Consumed Per Week',
    xyDataSet
))
frame.setSize(800, 600)
frame.visible = true