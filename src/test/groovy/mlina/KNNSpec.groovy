package mlina

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import spock.lang.Specification
import spock.lang.Unroll

import static mlina.KNN.*

class KNNSpec extends Specification {

    @Unroll def 'classify0(#input, _, #k)'() {
        setup:
        def data = createDataSet()

        expect:
        KNN.classify0(input, data, k) == result

        where:
        input      | k || result
        [1.5, 0.9] | 3 || 'A'
        [0.5, 0.9] | 3 || 'A'
        [0.5, 0.1] | 3 || 'B'
    }

    def 'autoNorm'() {
        setup:
        def data = createDataSet()

        when:
        def (Array2DRowRealMatrix norms, ranges, mins) = autoNorm(data)

        then:
        norms == new Array2DRowRealMatrix([
            [1.0, 1.0],
            [1.0, 0.9090909090909091],
            [0.0, 0.0],
            [0.0, 0.09090909090909091]
        ] as double[][])

        ranges == [1.0, 1.1]

        mins == [0.0, 0.0]
    }

    def 'datingClass testing'() {
        setup:
        def hoRatio = 0.10
        def data = fileData('/datingTestSet.txt')

        def (Array2DRowRealMatrix norms, ranges, mins) = autoNorm(data)

        int testVectors = norms.rowDimension * hoRatio
        int errorCount = 0

        when:
        testVectors.times { i ->
            def classification = classify0(norms.getRow(i) as List<Double>, new Tuple2<List<String>, Array2DRowRealMatrix>(data.first, norms), 3)
            println "Classifier came back with: ${classification}, the real answer is: ${data.first[i]}"
            if (classification != data.first[i]) {
                errorCount++
            }
        }

        then:
        errorCount == 2
    }

    def 'dating classification'() {
        expect:
        KNN.classifyPerson(videoGames, miles, iceCream) == result

        where:
        videoGames | miles    | iceCream || result
        100        | 10000    | 10        | 'smallDoses'
        68846      | 9.974715 | 0.669787 || 'smallDoses'
    }
}
