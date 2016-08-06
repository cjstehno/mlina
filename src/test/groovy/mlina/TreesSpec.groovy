package mlina

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static mlina.Trees.chooseBestFeatureToSplit
import static mlina.Trees.classify
import static mlina.Trees.createDataSet
import static mlina.Trees.createTree
import static mlina.Trees.retrieveTree
import static mlina.Trees.splitDataSet

class TreesSpec extends Specification {

    @Rule public TemporaryFolder folder = new TemporaryFolder()

    def 'calcShannonEntropy'() {
        setup:
        def data = createDataSet()

        when:
        double entropy = Trees.calcShannonEntropy(data.second)

        then:
        entropy == 0.9709505944546686
    }

    @Unroll def 'splitDataSet(_, #axis, #value)'() {
        setup:
        def data = createDataSet()

        expect:
        splitDataSet(data.second, axis, value) == result

        where:
        axis | value || result
        0    | 1     || [[1, 'yes'], [1, 'yes'], [0, 'no']]
        0    | 0     || [[1, 'no'], [1, 'no']]
    }

    def 'chooseBestFeature'() {
        setup:
        def data = createDataSet()

        when:
        def best = chooseBestFeatureToSplit(data.second)

        then:
        best == 0
    }

    def 'createTree'() {
        setup:
        def data = createDataSet()

        when:
        def tree = createTree(data.second, data.first)

        then:
        tree == ['no surfacing': [1: [flippers: [1: 'yes', 0: 'no']], 0: 'no']]
    }

    def 'classification'() {
        setup:
        def data = createDataSet()

        expect:
        classify(retrieveTree(treeIdx), data.first, [1, 0]) == result

        where:
        treeIdx | input  || result
        0       | [1, 0] || 'no'
        0       | [1, 1] || 'no'
    }

    def 'serialization'() {
        setup:
        def file = folder.newFile()
        def tree = retrieveTree(0)

        when:
        Trees.storeTree(tree, file.path)

        then:
        file.exists()
        file.length()

        when:
        def reloaded = Trees.grabTree(file.path)

        then:
        tree == reloaded
    }
}
