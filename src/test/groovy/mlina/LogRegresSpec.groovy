package mlina

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import spock.lang.Specification

import static mlina.LogRegres.gradAscent
import static mlina.LogRegres.loadDataSet
import static mlina.LogRegres.stocGradAscent0

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

    def 'stocastic gradient ascent'() {
        setup:
        def data = loadDataSet()

        when:
        def weights = stocGradAscent0(data.first, data.second)

        then:
        weights == new ArrayRealVector([1.0170200728876158, 0.859143479425245, -0.36579921045742] as double[])
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
}
