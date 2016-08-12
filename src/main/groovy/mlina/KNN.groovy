package mlina

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.data.xy.DefaultXYDataset
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class KNN {

    static Tuple2<List<String>, INDArray> createDataSet() {
        new Tuple2(
            ['A', 'A', 'B', 'B'],
            Nd4j.create([
                [1.0, 1.1],
                [1.0, 1.0],
                [0.0, 0.0],
                [0.0, 0.1]
            ] as double[][])
        )
    }

    static String classify0(INDArray inX, Tuple2<List<String>, INDArray> dataSet, int k) {
        def distances = []

        dataSet.second.rows().times { row ->
            distances << new Tuple2<>(
                dataSet.first[row],
                dataSet.second.getRow(row).distance2(inX)
            )
        }

        distances.sort { it.second }.take(k).countBy { it.first }.max { it.value }.key
    }

    static List autoNorm(Tuple2<List<String>, INDArray> dataSet) {
        def mins = []
        def maxs = []

        dataSet.second.columns().times { c ->
            dataSet.second.getColumn(c).with { column ->
                mins << column.minNumber()
                maxs << column.maxNumber()
            }
        }

        def ranges = []
        maxs.eachWithIndex { m, i ->
            ranges << m - mins[i]
        }

        INDArray normData = Nd4j.create(dataSet.second.rows(), dataSet.second.columns())

        dataSet.second.columns().times { c ->
            normData.putColumn(c, dataSet.second.getColumn(c).sub(mins[c] as double).div((maxs[c] - mins[c]) as double))
        }

        [normData, ranges, mins]
    }

    static Tuple2<List<String>, INDArray> fileData(String filename) {
        def groups = []
        def rows = []

        KNN.getResource(filename).eachLine { String line ->
            def cols = line.trim().split('\t')
            groups << cols[-1]
            rows << (cols[0..(-2)] as double[])
        }

        new Tuple2(groups, Nd4j.create(rows as double[][]))
    }

    static String classifyPerson(double playingVg, double ffMiles, double iceCream) {
        def dataSet = fileData('/datingTestSet.txt')
        def (normData, double[] ranges, double[] mins) = autoNorm(dataSet)

        classify0(Nd4j.create([
            (ffMiles - mins[0]) / ranges[0],
            (playingVg - mins[1]) / ranges[1],
            (iceCream - mins[2]) / ranges[2]
        ] as double[]), dataSet, 3)
    }

    static void plotDataSet(Tuple2<List<String>, INDArray> data, File file) {
        def groupIndices = [:]
        data.first.eachWithIndex { g, i ->
            if (groupIndices.containsKey(g)) {
                groupIndices[g] << i
            } else {
                groupIndices[g] = [i]
            }
        }

        def xyDataSet = new DefaultXYDataset()

        groupIndices.each { String g, indices ->
            def groupMtx = data.second.getRows(indices as int[])
            println groupMtx
            xyDataSet.addSeries(g, [groupMtx.getColumn(1).data().asDouble(), groupMtx.getColumn(2).data().asDouble()] as double[][])
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
