package mlina

import groovy.transform.TypeChecked
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor
import org.apache.commons.math3.linear.RealVector
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.annotations.XYLineAnnotation
import org.jfree.data.xy.DefaultXYDataset

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static java.lang.Math.exp

class LogRegres {

    static Tuple2<List<List<Double>>, List<Integer>> loadDataSet() {
        List<List<Double>> dataMat = []
        List<Integer> labelMat = []

        LogRegres.getResource('/testSet.txt').eachLine { String line ->
            def lineArr = line.trim().split()
            dataMat << [1.0, lineArr[0] as double, lineArr[1] as double]
            labelMat << (lineArr[2] as int)
        }

        new Tuple2<List<List<Double>>, List<Integer>>(dataMat, labelMat)
    }

    static Array2DRowRealMatrix gradAscent(dataMatIn, classLabels) {
        Array2DRowRealMatrix dataMatrix = new Array2DRowRealMatrix(dataMatIn as double[][])
        Array2DRowRealMatrix labelMat = new Array2DRowRealMatrix(classLabels as double[])

        int n = dataMatrix.columnDimension
        double alpha = 0.001
        int maxCycles = 500

        Array2DRowRealMatrix weights = new Array2DRowRealMatrix(n, 1)
        weights.setColumn(0, (0..<n).collect { 1.0 } as double[])

        (0..<maxCycles).each { k ->
            Array2DRowRealMatrix h = dataMatrix.copy().multiply(weights)
            h.walkInOptimizedOrder(new SigmoidWalker())

            Array2DRowRealMatrix error = labelMat.subtract(h)
            weights = weights.add(dataMatrix.transpose().scalarMultiply(alpha).multiply(error))
        }

        weights
    }

    static RealVector stocGradAscent0(List<List<Double>> dataMat, List<Integer> classLabels) {
        Array2DRowRealMatrix dataMatrix = new Array2DRowRealMatrix(dataMat.size(), dataMat[0].size())
        dataMat.eachWithIndex { row, idx ->
            dataMatrix.setRow(idx, row as double[])
        }

        RealVector labelVec = new ArrayRealVector(classLabels as double[])

        int m = dataMatrix.rowDimension
        int n = dataMatrix.columnDimension
        double alpha = 0.01

        RealVector weights = new ArrayRealVector(n, 1.0)

        (0..<m).each { k ->
            double h = SigmoidFunction.instance.value(dataMatrix.getRowVector(k).ebeMultiply(weights).toArray().sum())

            double error = labelVec.getEntry(k) - h
            weights = weights.add(dataMatrix.getRowVector(k).mapMultiply(alpha * error))
        }

        weights
    }

    static RealVector stocGradAscent1(List<List<Double>> dataMat, List<Integer> classLabels, int numIter=150){
        int m = dataMat.size()
        int n = dataMat[0].size()

        Random random = new Random()

        RealVector weights = new ArrayRealVector(n, 1.0)

        (0..<numIter).each { j->
            def dataIndex = (0..<m)
            (0..<m).each { i->
                double alpha = 4/(1.0+j+i)+0.01
                int randIndex = random.nextInt(dataIndex.size())

                // FIXME: this is where I left off
                double h = SigmoidFunction.sigmoid(dataMat[randIndex] )
            }
        }
    }

    /*
def stocGradAscent1(dataMatrix, classLabels, numIter=150):
    m,n = shape(dataMatrix)
    weights = ones(n)
    for j in range(numIter):
        dataIndex = range(m)
        for i in range(m):
            alpha = 4/(1.0+j+i)+0.01
            randIndex = int(random.uniform(0,len(dataIndex)))
            h = sigmoid(sum(dataMatrix[randIndex]*weights))
            error = classLabels[randIndex] - h
            weights = weights + alpha * error * dataMatrix[randIndex]
            del(dataIndex[randIndex])
    return weights

    [ 14.286994     1.12616186  -2.23537849]
     */

    static void plotBestFit(Array2DRowRealMatrix wei, File file) {
        def data = loadDataSet()
        def dataArr = data.first
        def n = dataArr.size()
        double[] weights = wei.getColumn(0)

        def xcord1 = []
        def ycord1 = []
        def xcord2 = []
        def ycord2 = []

        (0..<n).each { i ->
            if (data.second[i] == 1) {
                xcord1 << dataArr[i][1]
                ycord1 << dataArr[i][2]
            } else {
                xcord2 << dataArr[i][1]
                ycord2 << dataArr[i][2]
            }
        }

        def xyDataSet = new DefaultXYDataset()
        xyDataSet.addSeries('X1', [xcord1, ycord1] as double[][])
        xyDataSet.addSeries('X2', [xcord2, ycord2] as double[][])

        JFreeChart chart = ChartFactory.createScatterPlot('LogRegres', 'X', 'Y', xyDataSet)

        double y1 = (-weights[0] - weights[1] * (-3.0)) / weights[2]

        double y2 = (-weights[0] - weights[1] * (3.0)) / weights[2]

        chart.getXYPlot().addAnnotation(new XYLineAnnotation(
            -3.0,
            y1,
            3.0,
            y2,
        ))

        BufferedImage image = chart.createBufferedImage(800, 600)
        ImageIO.write(image, 'png', file)
    }
}

@TypeChecked
class SigmoidWalker extends DefaultRealMatrixChangingVisitor {

    private final SigmoidFunction fn = new SigmoidFunction()

    @Override
    double visit(int row, int column, double value) {
        fn.value(value)
    }
}

@TypeChecked @Singleton
class SigmoidFunction implements UnivariateFunction {

    @Override
    double value(double v) {
        sigmoid(v)
    }

    static double sigmoid(double inX) {
        1.0 / (1 + exp(-inX))
    }
}
