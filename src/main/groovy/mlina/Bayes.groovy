package mlina

import org.apache.commons.math3.analysis.function.Log
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealVector

import static org.apache.commons.math3.util.FastMath.log

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
        double pAbusive = trainCategory.sum() / numTrainDocs

        ArrayRealVector p0Num = new ArrayRealVector(numWords, 1.0)
        ArrayRealVector p1Num = new ArrayRealVector(numWords, 1.0)

        double p0Denom = 2.0
        double p1Denom = 2.0

        numTrainDocs.times { i ->
            if (trainCategory[i] == 1) {
                p1Num = p1Num.add(new ArrayRealVector(trainMatrix[i] as double[]))
                p1Denom += trainMatrix[i].sum()
            } else {
                p0Num = p0Num.add(new ArrayRealVector(trainMatrix[i] as double[]))
                p0Denom += trainMatrix[i].sum()
            }
        }

        RealVector p1Vect = p1Num.mapDivide(p1Denom).map(new Log())
        RealVector p0Vect = p0Num.mapDivide(p0Denom).map(new Log())

        [p0Vect, p1Vect, pAbusive]
    }

    static int classifyNB(RealVector vec2Classify, RealVector p0Vec, RealVector p1Vec, double pClass1) {
        double p1 = vec2Classify.ebeMultiply(p1Vec).toArray().sum() + log(pClass1)
        double p0 = vec2Classify.ebeMultiply(p0Vec).toArray().sum() + log(1.0 - pClass1)
        p1 > p0 ? 1 : 0
    }

    static List<Integer> bagOfWords2VecMN(final List<String> vocabList, final List<Integer> inputSet) {
        def returnVec = new int[vocabList.size()]
        inputSet.each { word ->
            if (word in vocabList) {
                returnVec[vocabList.indexOf(word)] += 1
            }
        }
        returnVec as List<Integer>
    }

    static List<String> textParse(String bigString) {
        bigString.split(/\W* |\//).collect {
            it.toLowerCase().replaceAll(/\p{Punct}/, '').trim()
        }.findAll { it.trim().length() > 2 }
    }
}
