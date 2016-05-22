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



