package mlina

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.Executors

import static mlina.LogRegres.*

class LogRegresSpec extends Specification {

    def 'gradient ascent'() {
        setup:
        def data = loadDataSet()

        when:
        def weights = gradAscent(data.first, data.second)

        then:
        weights == new Array2DRowRealMatrix([
            [4.124143489627893] as double[],
            [0.48007329288424455] as double[],
            [-0.6168481970344017] as double[]
        ] as double[][])
    }

    def 'stocastic gradient ascent 0'() {
        setup:
        def data = loadDataSet()

        when:
        def weights = stocGradAscent0(data.first, data.second)

        then:
        weights == new ArrayRealVector([1.0170200728876158, 0.859143479425245, -0.36579921045742] as double[])
    }

    def 'stocastic gradient ascent 1'() {
        setup:
        def data = loadDataSet()

        when:
        def weights = stocGradAscent1(data.first, data.second)

        then:
        println weights
        // slightly random, but in the area of
        //        weights == new ArrayRealVector([14.795935999006952, 1.2401709252683266, -2.132412085384009] as double[])
    }

    def 'plotting'() {
        setup:
        def data = loadDataSet()
        def weights = gradAscent(data.first, data.second)
        def file = new File(System.getProperty('user.home'), 'logregres-plot.png')

        when:
        LogRegres.plotBestFit(weights, file)

        then:
        file.exists()
    }

    def 'horse colic testing'() {
        when:
        def exec = Executors.newFixedThreadPool(8)

        def futures = []
        10.times {
            futures << exec.submit({ colicTesting() } as Callable<Double>)
        }

        def rates = []
        futures.each { f ->
            rates << f.get()
        }

        then:
        println "After 10 iterations the average error rate is: ${rates.sum() / 10}"
    }

    private double colicTesting() {
        def trainingSet = []
        def trainingLabels = []

        LogRegres.getResource('/horseColicTraining.txt').eachLine { String line ->
            def currLine = line.trim().split('\t')
            def lineArr = []
            (0..<21).each { i ->
                lineArr << (currLine[i] as double)
            }
            trainingSet << lineArr
            trainingLabels << (currLine[21] as double)
        }

        def trainWeights = stocGradAscent1(trainingSet, trainingLabels, 500)
        def errorCount = 0
        def numTestVec = 0.0

        LogRegres.getResource('/horseColicTest.txt').eachLine { String line ->
            numTestVec += 1.0
            def curLine = line.trim().split('\t')
            def lineArr = []
            (0..<21).each { i ->
                lineArr << (curLine[i] as double)
            }
            if (classifyVector(lineArr as double[], trainWeights) != (curLine[21] as int)) {
                errorCount += 1
            }
        }

        def errorRate = (errorCount as double) / numTestVec
        println "Error rate of this test is: $errorRate"

        return errorRate
    }
}

/*

the error rate of this test is: 0.343284
the error rate of this test is: 0.432836
the error rate of this test is: 0.417910
the error rate of this test is: 0.402985
the error rate of this test is: 0.373134
the error rate of this test is: 0.402985
the error rate of this test is: 0.388060
the error rate of this test is: 0.298507
the error rate of this test is: 0.343284
the error rate of this test is: 0.462687
after 10 iterations the average error rate is: 0.386567
 */