package mlina

import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealVector
import spock.lang.Specification
import spock.lang.Unroll

import static mlina.Bayes.*

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
            -1.8718021769015913, -2.5649493574615367, -2.5649493574615367, -2.5649493574615367, -2.5649493574615367, -2.5649493574615367,
            -2.5649493574615367, -3.258096538021482, -3.258096538021482, -3.258096538021482, -2.159484249353372, -2.5649493574615367,
            -3.258096538021482, -3.258096538021482, -2.5649493574615367, -2.5649493574615367, -2.5649493574615367, -2.5649493574615367,
            -2.5649493574615367, -2.5649493574615367, -2.5649493574615367, -3.258096538021482, -3.258096538021482, -3.258096538021482,
            -2.5649493574615367, -2.5649493574615367, -2.5649493574615367, -2.5649493574615367, -2.5649493574615367, -3.258096538021482,
            -3.258096538021482, -3.258096538021482
        ] as double[])

        p1V == new ArrayRealVector([
            -3.044522437723423, -1.9459101490553135, -3.044522437723423, -3.044522437723423, -3.044522437723423, -3.044522437723423,
            -3.044522437723423, -2.3513752571634776, -2.3513752571634776, -2.3513752571634776, -2.3513752571634776, -2.3513752571634776,
            -2.3513752571634776, -1.6582280766035324, -3.044522437723423, -3.044522437723423, -3.044522437723423, -3.044522437723423,
            -3.044522437723423, -3.044522437723423, -2.3513752571634776, -2.3513752571634776, -1.9459101490553135, -2.3513752571634776,
            -3.044522437723423, -3.044522437723423, -3.044522437723423, -3.044522437723423, -3.044522437723423, -2.3513752571634776,
            -2.3513752571634776, -2.3513752571634776
        ] as double[])

        pAb == 0.5
    }

    def 'classifyNB'() {
        setup:
        def data = loadDataSet()
        def myVocabList = createVocabList(data.first)

        def trainMat = data.first.collect { postinDoc -> setOfWords2Vec(myVocabList, postinDoc) }

        def (RealVector p0V, RealVector p1V, double pAb) = trainNB0(trainMat, data.second)

        expect:
        classifyNB(
            new ArrayRealVector(setOfWords2Vec(myVocabList, input) as double[]),
            p0V, p1V, pAb
        ) == result

        where:
        input                       || result
        ['love', 'my', 'dalmatian'] || 0
        ['stupid', 'garbage']       || 1
    }
}
