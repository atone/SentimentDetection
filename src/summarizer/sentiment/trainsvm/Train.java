package summarizer.sentiment.trainsvm;

import java.util.ArrayList;

/**
 * @author wyq
 *
 */
public class Train {
	
	public static String trainCorpus="./resource/trainCorpus/";
	
	
	public void setTrainCorpus(String traincorpusPath){
		trainCorpus=traincorpusPath;
	}
	/**
	 * 为了得到BagOfWords+SVM最终模型
	 */
	public static void trainBagOfwWords() {
		for (int i = 1; i <= 17; i++) {
			trainBagOfwWords(i);
		}

	}
	
	/**
	 * 为了得到BagOfWords+SVM最终模型
	 */
	public static void trainBagOfwWords(int aspectID) {
		String trainCor = trainCorpus+"a" + aspectID;
		SVMBagOfWords svm = new SVMBagOfWords();
		svm.setTrainCorpus(trainCor);
		svm.setAspectID(aspectID);
		svm.trainSoftMax(true);

	}

	
	/**
	 * @param pos
	 * @param neu
	 * @param neg
	 * @param k
	 * @param num
	 * @return 得到k折交叉验证时第k个svm模型
	 */
	public static SVMBagOfWords crossValSVMBagOfWords(ArrayList<String> pos,
			ArrayList<String> neu, ArrayList<String> neg, int k, int num,int aspectID) {
		
		SVMBagOfWords svm = new SVMBagOfWords();
		svm.aspectID=aspectID;
		
		//构造k折的第num折的训练集
		ArrayList<String> posi = new ArrayList<String>();
		for (int i = 0; i < pos.size(); i++)
			if(i<pos.size()*1.0/k*(num-1)||i>pos.size()*1.0/k*num-1)
				posi.add(pos.get(i));
		ArrayList<String> neur = new ArrayList<String>();
		for (int i = 0; i < neu.size() * (1 - 1.0 / k); i++)
			if(i<neu.size()*1.0/k*(num-1)||i>neu.size()*1.0/k*num-1)
				neur.add(neu.get(i));
		ArrayList<String> nega = new ArrayList<String>();
		for (int i = 0; i < neg.size() * (1 - 1.0 / k); i++)
			if(i<neg.size()*1.0/k*(num-1)||i>neg.size()*1.0/k*num-1)
				nega.add(neg.get(i));
		svm.setTrainCorpus(posi, neur, nega);
//		svm.trainSoftMaxForCV();
		return svm;
	}
}
