package mlina

import static mlina.Utils.divideAllBy
import static mlina.Utils.valueAddition
import static mlina.Utils.zeros

class Bayes {

    static Tuple2<List<String>, List<Integer>> loadDataSet() {
        new Tuple2<>(
            [
                ['my', 'dog', 'has', 'flea', 'problems', 'help', 'please'],
                ['maybe', 'not', 'take', 'him', 'to', 'dog', 'park', 'stupid'],
                ['my', 'dalmatian', 'is', 'so', 'cute', 'I', 'love', 'him'],
                ['stop', 'posting', 'stupid', 'worthless', 'garbage'],
                ['mr', 'licks', 'ate', 'my', 'steak', 'how', 'to', 'stop', 'him'],
                ['quit', 'buying', 'worthless', 'dog', 'food', 'stupid']
            ],
            [0, 1, 0, 1, 0, 1] // 1 is abusive, 0 not
        )
    }

    static List<String> createVocabList(final List<String> dataSet) {
        Set<String> vocabSet = [] as Set<String>

        dataSet.each { document ->
            vocabSet.addAll(document)
        }

        vocabSet as List
    }

    static List<Integer> setOfWords2Vec(final List<String> vocab, final List<String> input) {
        def returnVec = new int[vocab.size()]
        input.each { word ->
            if (word in vocab) {
                returnVec[vocab.indexOf(word)] = 1
            } else {
                throw new IllegalArgumentException("the word: $word is not in my Vocabulary!")
            }
        }
        returnVec as List<Integer>
    }

    static List trainNB0(final List<List<Integer>> trainMatrix, final List<Integer> trainCategory) {
        int numTrainDocs = trainMatrix.size()
        int numWords = trainMatrix[0].size()
        def pAbusive = trainCategory.sum() / numTrainDocs

        List<Number> p0Num = zeros(numWords)
        List<Number> p1Num = zeros(numWords)

        double p0Denom = 0.0
        double p1Denom = 0.0

        numTrainDocs.times { i ->
            if (trainCategory[i] == 1) {
                p1Num = valueAddition(p1Num, trainMatrix[i])
                p1Denom += trainMatrix[i].sum()
            } else {
                p0Num = valueAddition(p0Num, trainMatrix[i])
                p0Denom += trainMatrix[i].sum()
            }
        }

        List<Number> p1Vect = divideAllBy(p1Num, p1Denom)
        List<Number> p0Vect = divideAllBy(p0Num, p0Denom)

        [p0Vect, p1Vect, pAbusive]
    }

}

class Utils {

    static List<Double> zeros(int len) {
        (0..<len).collect { 0 }
    }

    static List<Number> valueAddition(final List<Number> first, final List<Number> second) {
        assert first.size() == second.size()
        def output = []
        first.eachWithIndex { Number value, int i ->
            output << (value + second[i])
        }
        output
    }

    static List<Number> divideAllBy(final List<Number> numerators, final Number denominator) {
        numerators.collect { Number num -> (num / denominator) }
    }
}