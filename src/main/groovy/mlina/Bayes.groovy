package mlina

import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealVector

import static mlina.Utils.*

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

        ArrayRealVector p0Num = new ArrayRealVector(numWords)
        ArrayRealVector p1Num = new ArrayRealVector(numWords)

        double p0Denom = 0.0
        double p1Denom = 0.0

        numTrainDocs.times { i ->
            if (trainCategory[i] == 1) {
                p1Num = p1Num.add(new ArrayRealVector(trainMatrix[i] as double[]))
                p1Denom += trainMatrix[i].sum()
            } else {
                p0Num = p0Num.add(new ArrayRealVector(trainMatrix[i] as double[]))
                p0Denom += trainMatrix[i].sum()
            }
        }

        RealVector p1Vect = p1Num.mapDivide(p1Denom)
        RealVector p0Vect = p0Num.mapDivide(p0Denom)

        [p0Vect, p1Vect, pAbusive]
    }
}
