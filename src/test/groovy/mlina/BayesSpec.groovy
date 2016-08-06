package mlina

import org.apache.commons.math3.linear.ArrayRealVector
import spock.lang.Specification
import spock.lang.Unroll

import static mlina.Bayes.createVocabList
import static mlina.Bayes.loadDataSet
import static mlina.Bayes.setOfWords2Vec
import static mlina.Bayes.trainNB0

class BayesSpec extends Specification {

    def 'create vocablist with dataset'() {
        setup:
        def data = loadDataSet()

        when:
        def myVocabList = createVocabList(data.first)

        then:
        myVocabList == [
            'my', 'dog', 'has', 'flea', 'problems', 'help', 'please', 'maybe', 'not', 'take', 'him',
            'to', 'park', 'stupid', 'dalmatian', 'is', 'so', 'cute', 'I', 'love', 'stop', 'posting',
            'worthless', 'garbage', 'mr', 'licks', 'ate', 'steak', 'how', 'quit', 'buying', 'food'
        ]
    }

    @Unroll def 'setOfWords2Vec(#index)'() {
        setup:
        def data = loadDataSet()
        def myVocabList = createVocabList(data.first)

        expect:
        setOfWords2Vec(myVocabList, data.first[index]) == expected

        where:
        index || expected
        0     || [1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        1     || [0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        2     || [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        3     || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0]
        4     || [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0]
        5     || [0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1]
    }

    def 'trainNB0'() {
        setup:
        def data = loadDataSet()
        def myVocabList = createVocabList(data.first)

        def trainMat = data.first.collect { postinDoc -> setOfWords2Vec(myVocabList, postinDoc) }

        when:
        def (p0V, p1V, pAb) = trainNB0(trainMat, data.second)

        then:
        p0V == new ArrayRealVector([
            0.125, 0.041666666666666664, 0.041666666666666664, 0.041666666666666664, 0.041666666666666664, 0.041666666666666664, 0.041666666666666664,
            0.0, 0.0, 0.0, 0.08333333333333333, 0.041666666666666664, 0.0, 0.0, 0.041666666666666664, 0.041666666666666664, 0.041666666666666664,
            0.041666666666666664, 0.041666666666666664, 0.041666666666666664, 0.041666666666666664, 0.0, 0.0, 0.0, 0.041666666666666664,
            0.041666666666666664, 0.041666666666666664, 0.041666666666666664, 0.041666666666666664, 0.0, 0.0, 0.0
        ] as double[])

        p1V == new ArrayRealVector([
            0.0, 0.10526315789473684, 0.0, 0.0, 0.0, 0.0, 0.0, 0.05263157894736842, 0.05263157894736842, 0.05263157894736842, 0.05263157894736842,
            0.05263157894736842, 0.05263157894736842, 0.15789473684210525, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.05263157894736842, 0.05263157894736842,
            0.10526315789473684, 0.05263157894736842, 0.0, 0.0, 0.0, 0.0, 0.0, 0.05263157894736842, 0.05263157894736842, 0.05263157894736842
        ] as double[])

        pAb == 0.5
    }
}
