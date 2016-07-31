import org.apache.commons.math3.linear.Array2DRowRealMatrix
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

def autoNorm(dataSet) {
    def mins = []
    def maxs = []
    dataSet.second.columnDimension.times { c ->
        mins << (dataSet.second.getColumn(c) as Collection).min()
        maxs << (dataSet.second.getColumn(c) as Collection).max()
    }

    def ranges = []
    maxs.eachWithIndex { m, i ->
        ranges << m - mins[i]
    }

    def normData = new Array2DRowRealMatrix(dataSet.second.rowDimension, dataSet.second.columnDimension)

    dataSet.second.columnDimension.times { c ->
        normData.setColumn(c, dataSet.second.getColumn(c).collect { d ->
            (d - mins[c]) / (maxs[c] - mins[c])
        } as double[])
    }

    [normData, ranges, mins]
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

def datingClassTest() {
    def hoRatio = 0.10
    def data = fileData('../files/datingTestSet.txt')
    def (norms, ranges, mins) = autoNorm(data)
    int testVectors = norms.rowDimension * hoRatio
    int errorCount = 0
    testVectors.times { i ->
        def classification = classify0(norms.getRow(i), new Tuple2<>(data.first, norms), 3)
        println "Classifier came back with: ${classification}, the real answer is: ${data.first[i]}"
        if (classification != data.first[i]) {
            errorCount++
        }
    }
    println "Total error rate: ${errorCount / (double) testVectors}"
}

def classifyPerson(){
    Console console = Console.newInstance()

    double playingVg = prompt(console, 'Percentage of time playing video games? ')
    double ffMiles = prompt(console, 'Frequent flier miles earned per year? ')
    double iceCream = prompt(console, 'Liters of ice cream consumed per year? ')

    def dataSet = fileData('../files/datingTestSet.txt')
    def (normData, ranges, mins) = autoNorm(dataSet)

    def classification = classify0([ffMiles, playingVg, iceCream] as double[], dataSet, 3)

    console.writer().println "You will probably like this person: ${classification}"
    console.writer().flush()
}

double prompt(Console console, String text){
    console.writer().print text
    console.writer().flush()
    console.readLine()?.trim() as double
}

//def data = fileData('../files/datingTestSet.txt')
//
//def groupIndices = [:]
//data.first.eachWithIndex { g, i ->
//    if (groupIndices.containsKey(g)) {
//        groupIndices[g] << i
//    } else {
//        groupIndices[g] = [i]
//    }
//}
//
//def xyDataSet = new DefaultXYDataset()
//
//groupIndices.each { g, indices ->
//    def groupMtx = data.second.getSubMatrix(indices as int[], [0, 1, 2] as int[])
//    xyDataSet.addSeries(g, [groupMtx.getColumn(1), groupMtx.getColumn(2)] as double[][])
//}
//
//def frame = new ChartFrame('Charting', ChartFactory.createScatterPlot(
//    'Dating Test',
//    'Percentage Time Spent Playing Video Games',
//    'Liters of Ice Cream Consumed Per Week',
//    xyDataSet
//))
//frame.setSize(800, 600)
//frame.visible = true
//
//datingClassTest()

classifyPerson()