# Sentiment Detection

In this section, we need to detect the sentiment orientation of the phrases. The available sentiment orientation of a phrase is *positive* (denoted as 1) and *negative* (denoted as -1). In order to do this, we train an SVM classifier.

## Feature Selection

We use the chi-square test for feature selection. Specifically, for each term $t$, the chi-square is calculated as:

$$\chi^2=\frac{N(N_{11}N_{00}-N_{10}N_{01})}{(N_{11}+N_{01})(N_{11}+N_{10})(N_{10}+N_{00})(N_{01}+N_{00})}$$

where $N$ denotes the total number of short sentences, $N_{ts}$ are the counts of short sentences that have the value $e_t$ (whether term $t$ is in the short sentence, it takes the value 1 or 0) and $e_s$ (the sentiment orientation of the short sentence, it takes the value 1 or 0) that indicated by two subscripts.

We rank each term by their chi-square value in descend order and choose the first $k$ terms as the feature. Specifically, for each short sentence, the feature vector is a vector of binary values indicating whether the corresponding feature term is in this short sentence or not.

## Train

To train the SVM classifier, we use the review data for cell phones from [jd.com](http://www.jd.com), where each review is tagged as *good* or *bad*. We use this tag information as the sentiment label for the reviews. After splitting reviews into short sentences, we get 176,640 short sentences, 73.2% of them are labeled positive and 26.8% are labeled negative. Since the same opinion word may have different orientations in different aspects (e.g. the phrase `the screen resolution is high` is positive, but the phrase `the price is high` is negative), we train an SVM classifier for each of the 17 aspects separately. The training data is available in `data/trainCorpus/` folder. The trained model is available in `resource/svm/` folder.

## Test

We randomly select 5500 short sentences for testing. The test set is not used for training. The data for Exp A and Exp B is available here:

- [dataset for Exp A](https://raw.githubusercontent.com/atone/SentimentDetection/master/data/test/exp_a.txt)
- [dataset for Exp B](https://raw.githubusercontent.com/atone/SentimentDetection/master/data/test/exp_b.txt)

## Code Explanation

To run this code, first clone it to your local disk: 

```
git clone https://github.com/atone/SentimentDetection.git
```

then download the model files for [THULAC](http://thulac.thunlp.org/message), unzip the model files and put it in `resource/thulac_models/`.

### Train the Model

By default, we have provided the trained model file, so you don't need to train it yourself. However, if you still want to train the SVM model, use the class `Train` at package `summarizer.sentiment.trainsvm`:

```java
public static void setTrainCorpus(String traincorpusPath)
```
set the data corpus path for training.

```java
public static void trainBagOfWords(int aspectID)
```
train the SVM model for the specific aspectID (range from 1 to 17).

To set where the model files are saved, use the class `SVMBagOfWords`:

```java
public static void setSVMFolder(String svmFolderPath)
```
set the folder where the SVM model files are stored, default is `./resource/svm/`

### Use the Model

To use the model, first set where to find the SVM model files:

```java
public static void setSVMFolder(String svmFolderPath)
```
by default, we have the trained model stored in `./resource/svm/`, so this step is optional.

The following two methods in `summarizer.sentiment.predict.OpinionModelSVM` are used for predict sentiment of phrases:

```java
public static int predict(String splitedText, String aspect)
public static List<Integer> predict(List<String> splitedTextList, String aspect)
```
The first method is used for predicting a single phrase, the result is 1 if positive and -1 if negative. The second method is used for predicting a list of phrases, the result is a list of results. The `aspect` parameter is used for denote the aspect of the phrase, e.g., if the aspect ID is 1, then pass `"a1"` as the parameter.

Note: the phrase must be POS tagged before calling the `predict` method. To do the POS tagging, use the static method in `thulac.segment.ThuLac`:

```java
public static String segment(String original)
```

For example, 

```java
String example = "我爱北京天安门。";
String tagged = ThuLac.segment(example);
System.out.println(tagged);
```

the result is: `我/r 爱/v 北京/ns 天安门/ns 。/w`.





