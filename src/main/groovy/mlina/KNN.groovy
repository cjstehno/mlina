package mlina

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.data.xy.DefaultXYDataset

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.apache.commons.math3.util.MathArrays.distance

class KNN {

    static Tuple2<List<String>, Array2DRowRealMatrix> createDataSet() {
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

    static String classify0(List<Double> inX, Tuple2<List<String>, Array2DRowRealMatrix> dataSet, int k) {
        def distances = []

        dataSet.second.rowDimension.times { row ->
            distances << new Tuple2<>(
                dataSet.first[row],
                distance(dataSet.second.getRow(row), inX as double[])
            )
        }

        distances.sort { it.second }.take(k).countBy { it.first }.max { it.value }.key
    }

    static List autoNorm(Tuple2<List<String>, Array2DRowRealMatrix> dataSet) {
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

    static Tuple2<List<String>, Array2DRowRealMatrix> fileData(String filename) {
        def groups = []
        def rows = []

        KNN.getResource(filename).eachLine { String line ->
            def cols = line.trim().split('\t')
            groups << cols[-1]
            rows << (cols[0..(-2)] as double[])
        }

        new Tuple2(groups, new Array2DRowRealMatrix(rows as double[][]))
    }

    static String classifyPerson(double playingVg, double ffMiles, double iceCream) {
        def dataSet = fileData('/datingTestSet.txt')
        def (normData, ranges, mins) = autoNorm(dataSet)

        println normData

        def classification = classify0([
            (ffMiles - mins[0]) / ranges[0],
            (playingVg - mins[1]) / ranges[1],
            (iceCream - mins[2]) / ranges[2]
        ] as List<Double>, dataSet, 3)

        classification
    }

    static void plotDataSet(Tuple2<List<String>, Array2DRowRealMatrix> data, File file) {
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

        JFreeChart chart = ChartFactory.createScatterPlot(
            'Dating Test',
            'Percentage Time Spent Playing Video Games',
            'Liters of Ice Cream Consumed Per Week',
            xyDataSet
        )

        //        def frame = new ChartFrame('Charting', chart)
        //        frame.setSize(800, 600)
        //        frame.visible = true

        BufferedImage image = chart.createBufferedImage(800, 600)
        ImageIO.write(image, 'png', file)
    }
}
