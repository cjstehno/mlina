package mlina

import groovy.transform.TypeChecked
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor

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
}

@TypeChecked
class SigmoidWalker extends DefaultRealMatrixChangingVisitor {

    @Override
    double visit(int row, int column, double value) {
        return sigmoid(value)
    }

    static double sigmoid(double inX) {
        1.0 / (1 + exp(-inX))
    }
}
