package summarizer.sentiment.trainsvm;

import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import jnisvmlight.TrainingParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author wyq
 *
 */
public class TrainSVM {
	private int N;
	private Review positive;
	private Review negative;
	private String modelName;
	private HashMap<String, Word> allWords;
	private ArrayList<Word> sortedWord;
	private HashMap<String, Integer> wordIDMap;
	public String modelFolder;

	/**
	 * @param modelFolder
	 * @param svmName
	 * @param positive
	 * @param negative
	 */
	public TrainSVM(String modelFolder, String svmName, Review positive,
			Review negative) {
		this.modelName = svmName;
		this.positive = positive;
		this.negative = negative;
		N = this.positive.getReviewDoc().size()
				+ this.negative.getReviewDoc().size();
		allWords = new HashMap<String, Word>();
		sortedWord = new ArrayList<Word>();
		System.out.println();
		if (!modelFolder.endsWith("/"))
			this.modelFolder = modelFolder + "/";
		else
			this.modelFolder = modelFolder;
		FileIO.buildFolder(this.modelFolder);
		if (!new File(modelFolder).exists())
			new File(this.modelFolder).mkdirs();
	}

	public HashMap<String, Integer> getWordIDMap() {
		return this.wordIDMap;
	}
	/**
	 * 使用卡方检验的方法进行特征选择，并将特征保存在本地
	 */
	public void saveFeature() {
		this.positive.updateAllWords(allWords);
		this.negative.updateAllWords(allWords);
		Iterator<Entry<String, Word>> iter = this.allWords.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, Word> entry = (Entry<String, Word>) iter.next();
			entry.getValue().setChi(N, this.positive.getReviewDoc().size(),
					this.negative.getReviewDoc().size());
			sortedWord.add(entry.getValue());
		}
		SortUtil.sortWord(sortedWord);
		FileIO.writeFeatureFile(sortedWord, this.modelFolder 
				+ "/feature");
	}

	/**
	 * 读取特征map到wordIDMap中
	 */
	public void readFeature() {
		wordIDMap = FileIO.readMapFile(this.modelFolder	+ "/feature");
	}

	/**
	 * 为训练集中的所有正类和负类进行特征值计算
	 * 
	 * @param method
	 *            0 代表卡方检验，1代表word2vector
	 */
	public void setFeatureVector(int method) {
		for (ReviewDoc tmp : this.positive.getReviewDoc()) {
			tmp.setFeatureVector(wordIDMap, method);
		}
		for (ReviewDoc tmp : this.negative.getReviewDoc()) {
			tmp.setFeatureVector(wordIDMap, method);
		}
	}

	public SVMLightModel trainSVM(Boolean writeTiFile) {
		SVMLightInterface trainer = new SVMLightInterface();
		LabeledFeatureVector[] traindata = new LabeledFeatureVector[this.positive
				.getReviewDoc().size() + this.negative.getReviewDoc().size()];
		SVMLightInterface.SORT_INPUT_VECTORS = true;
		int i = 0;
		for (; i < positive.getReviewDoc().size(); i++) {
			if (positive.getReviewDoc().get(i).dims.length > 0)
				traindata[i] = new LabeledFeatureVector(1, positive
						.getReviewDoc().get(i).dims, this.positive
						.getReviewDoc().get(i).values);
			// traindata[i].normalizeL2();
		}

		for (int j = 0; j < negative.getReviewDoc().size(); j++) {
			if (negative.getReviewDoc().get(j).dims.length > 0)
				traindata[i + j] = new LabeledFeatureVector(-1, negative
						.getReviewDoc().get(j).dims, this.negative
						.getReviewDoc().get(j).values);
			// traindata[i + j].normalizeL2();
		}
		TrainingParameters tp = new TrainingParameters();
		tp.getLearningParameters().verbosity = 1;
		System.out.println("TRAINING SVM-light MODEL ...");
		SVMLightModel svm = trainer.trainModel(traindata, tp);
		System.out.println(" Done.");
		if (!new File(this.modelFolder ).exists())
			new File(this.modelFolder).mkdirs();
		if (writeTiFile)
			svm.writeModelToFile(this.modelFolder + "/model");
		return svm;
	}
}
