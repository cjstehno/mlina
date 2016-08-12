package mlina

import groovy.transform.CompileStatic
import org.nd4j.linalg.api.iter.INDArrayIterator
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import spock.lang.Specification
import spock.lang.Unroll

import static mlina.KNN.*

class KNNSpec extends Specification {

    @Unroll def 'classify0(#input, _, #k)'() {
        setup:
        def data = createDataSet()

        expect:
        classify0(Nd4j.create(input as double[]), data, k) == result

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
        def (INDArray norms, ranges, mins) = autoNorm(data)

        then:
        norms == Nd4j.create([
            [1.0, 1.0],
            [1.0, 0.9090909090909091],
            [0.0, 0.0],
            [0.0, 0.09090909090909091]
        ] as double[][])

        ranges == [1.0, 1.100000023841858]
        mins == [0.0, 0.0]
    }

    def 'datingClass testing'() {
        setup:
        def hoRatio = 0.10
        def data = fileData('/datingTestSet.txt')

        def (INDArray norms, ranges, mins) = autoNorm(data)

        int testVectors = norms.rows() * hoRatio
        int errorCount = 0

        when:
        testVectors.times { i ->
            def classification = classify0(norms.getRow(i), new Tuple2<List<String>, INDArray>(data.first, norms), 3)
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
        classifyPerson(videoGames, miles, iceCream) == result

        where:
        videoGames | miles    | iceCream || result
        100        | 10000    | 10       || 'smallDoses'
        68846      | 9.974715 | 0.669787 || 'smallDoses'
    }

    def 'foo'() {
        setup:
        def array = Nd4j.create([[1, 2, 3], [4, 5, 6]] as double[][])

        when:
        def data = array.getColumn(1)
        def values = new INDArrayIterator(data).collect()

        then:
        println array
    }

    def 'plotting'() {
        setup:
        def data = fileData('/datingTestSet.txt')
        def file = new File(System.getProperty('user.home'), 'knn-plot.png')

        when:
        plotDataSet(data, file)

        then:
        file.exists()
    }
}

@CompileStatic
class Grid {

    private final data

    Grid(double[][] data) {
        this.data = data
    }
}