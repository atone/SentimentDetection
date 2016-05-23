package summarizer.sentiment.trainsvm;

import java.util.ArrayList;

/**
 * @author wyq
 *
 */
public class SVMBagOfWords extends SVM{
	private  String modelNames;
	private  SingleSVM svm;
	public static  String svmFolder = "./resource/svm/";
	public String trainCor;
	public int aspectID;
	public ArrayList<String> posi;
	public ArrayList<String> nega;

	/**
	 * @param trainCorpusFolderPath
	 *            设置训练数据集文件夹路径，并读取训练数据。
	 */
	public void setTrainCorpus(String trainCorpusFolderPath) {
		this.trainCor = trainCorpusFolderPath;
		this.posi = FileIO.readXmlFile(this.trainCor + "/positive");
		this.nega = FileIO.readXmlFile(this.trainCor + "/negative");
	}
	
	public static void setSVMFolder(String svmFolderPath){
		svmFolder=svmFolderPath;
	}

	/**
	 * @param pos
	 * @param neu
	 * @param neg
	 *            内存中设置训练数据
	 */
	public void setTrainCorpus(ArrayList<String> pos, ArrayList<String> neu,
			ArrayList<String> neg) {
		this.posi = pos;
		this.nega = neg;
	}

	public void setAspectID(int id) {
		this.aspectID = id;
	}
	
	public void readSVMModel() {
			svm=new SingleSVM(svmFolder,modelNames);
		
	}
	public void readSVMModel(String svmFolder) {
			svm=new SingleSVM(svmFolder, svmFolder);
	
	}

	public int predict(String review) {
		double res = svm.p_sigmod(review, 0);
		if(res>0.5)
			return 1;
		else
			return 0;
	}

	public void trainSoftMax(boolean writeTiFile) {
		Review positive = new Review(true);
		positive.build(posi);
		Review negative = new Review(true);
		negative.build(nega);

		TrainSVM positive_svm = new TrainSVM(svmFolder + "a" + this.aspectID,
				"positive", positive, negative);
		positive_svm.saveFeature();
		positive_svm.readFeature();
		positive_svm.setFeatureVector(0);
		svm=new SingleSVM(positive_svm.trainSVM(writeTiFile));
	}
	
	public void train(boolean writeTiFile) {
		FileIO.writeFile("positive\nneutral\nnegative", svmFolder+"/map");
		ArrayList<String> posi = FileIO.readListFile("./resource/positive");
		ArrayList<String> nega = FileIO.readListFile("./resource/negative");
		ArrayList<String> neur = FileIO.readListFile("./resource/neutral");
		Review positive = new Review(true);
		positive.build(posi);
		Review negative = new Review(true);
		negative.build(nega);
		Review neutral = new Review(true);
		neutral.build(neur);
		// 构造分类器positive为正例，negative+neutral为负例
		TrainSVM positive_svm = new TrainSVM(svmFolder, "positive", positive,
				Review.mergeReview(negative, neutral, false));
		positive_svm.saveFeature();
		positive_svm.readFeature();
		positive_svm.setFeatureVector(0);
		positive_svm.trainSVM(writeTiFile);
		// 构造分类器negative为正例，positive+neutral为负例
		TrainSVM negative_svm = new TrainSVM(svmFolder, "negative", negative,
				Review.mergeReview(positive, neutral, false));
		negative_svm.saveFeature();
		negative_svm.readFeature();
		negative_svm.setFeatureVector(0);
		negative_svm.trainSVM(writeTiFile);
		// 构造分类器neutral为正例，positive+neutral为负例
		TrainSVM neutral_svm = new TrainSVM(svmFolder, "neutral", neutral,
				Review.mergeReview(positive, negative, false));
		neutral_svm.saveFeature();
		neutral_svm.readFeature();
		neutral_svm.setFeatureVector(0);
		neutral_svm.trainSVM(writeTiFile);
	}
}
