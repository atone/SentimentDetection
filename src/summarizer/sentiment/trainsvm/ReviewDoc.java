package summarizer.sentiment.trainsvm;

import jnisvmlight.FeatureVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

//import tool.weibo.nlpir.Nlpir;

public class ReviewDoc {
	private static String vectorFile = "./resource/vector_sogouSina";
	private static HashMap<String, ArrayList<Double>> word2vector;

	private String content;
	private int totalWordNum = 0;
	private HashMap<String, Integer> wordMap;
	private HashMap<String, Double> tfMap;
	private Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
	private double[] docVector;
	private FeatureVector fv;
	public int[] dims;
	public double[] values;

	public ReviewDoc(String content) {
		this.content = content;
	}

	public static void initWord2vector() {
		ReviewDoc.word2vector = FileIO.readVectorFile(vectorFile);
	}

	public ReviewDoc setChi() {
		wordMap = new HashMap<String, Integer>();
		tfMap = new HashMap<String, Double>();
		setWordMap();
		setTFMap();
		return this;
	}

	public void setWordMap() {
//		String[] words = Nlpir.NlpirSegmentation(content).split(" ");
//		for (int i = 0; i < words.length; i++) {
//			Matcher m = p.matcher(words[i]);
//			if (m.find() && !StopWordUtil.isStopWord(words[i])) {
//				totalWordNum++;
//				if (wordMap.containsKey(words[i])) {
//					wordMap.put(words[i], wordMap.get(words[i]) + 1);
//				} else
//					wordMap.put(words[i], 1);
//			}
//
//		}
	}

	public HashMap<String, Integer> getWordMap() {
		return this.wordMap;
	}


	public void printWordsMap() {
		Iterator<Entry<String, Integer>> iter = this.wordMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iter.next();
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	public void setTFMap() {
		Iterator<Entry<String, Integer>> iter = this.wordMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>) iter.next();
			this.tfMap.put(entry.getKey(),
					entry.getValue() * 1.0 / Math.sqrt(totalWordNum));
		}
	}

	/**
	 * @param wordIDMap
	 * @param method
	 *            0 代表卡方检验，1代表word2vector
	 */
	public void setFeatureVector(HashMap<String, Integer> wordIDMap, int method) {
		switch (method) {
		case 0:
			Iterator<Entry<String, Double>> iter = this.tfMap.entrySet()
					.iterator();
			HashMap<Integer, Double> map = new HashMap<Integer, Double>();
			while (iter.hasNext()) {
				Entry<String, Double> entry = (Entry<String, Double>) iter
						.next();
				if (wordIDMap.containsKey(entry.getKey())) {
					map.put(wordIDMap.get(entry.getKey()), entry.getValue());
				}
			}
			dims = new int[map.size()];
			values = new double[map.size()];
			Iterator<Entry<Integer, Double>> it = map.entrySet().iterator();
			int i = 0;
			while (it.hasNext()) {
				Entry<Integer, Double> entry = (Entry<Integer, Double>) it
						.next();
				dims[i] = entry.getKey();
				values[i++] = entry.getValue();
			}
			this.fv = new FeatureVector(dims, values);
			break;
		case 1:
		
			break;
		}

	}

	public FeatureVector getFV() {
		return this.fv;
	}
}
