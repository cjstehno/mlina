package mlina

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import spock.lang.Specification

import static mlina.LogRegres.gradAscent
import static mlina.LogRegres.loadDataSet

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
}
