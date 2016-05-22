package summarizer.sentiment.trainsvm;

import jnisvmlight.SVMLightModel;

import java.io.File;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.HashMap;

public class SingleSVM {
	private SVMLightModel svm;
	private String modelName;
	private HashMap<String, Integer> wordIDMap;

	public SingleSVM(String modelFolder, String svmName) {
		this.modelName = svmName;
		wordIDMap = FileIO.readMapFile(modelFolder + this.modelName
				+ "/feature");
		try {
			svm = SVMLightModel.readSVMLightModelFromURL(new File(modelFolder
					+ modelName + "/model").toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public SingleSVM(SVMLightModel svm){
		this.svm=svm;
	}
	public SingleSVM(SVMLightModel svm,HashMap<String,Integer> wordIDMap){
		this.svm=svm;
		this.wordIDMap=wordIDMap;
	}

	public int predict(String str, int method) {
		ReviewDoc r = new ReviewDoc(str).setChi();
		r.setFeatureVector(this.wordIDMap, method);
		int label = -1;
		if (svm.classify(r.getFV()) > 0)
			label = 1;
		if (svm.classify(r.getFV()) < 0)
			label = 0;
		return label;
	}

	public double predictReview(String str, int method) {
		ReviewDoc r = new ReviewDoc(str).setChi();
		r.setFeatureVector(this.wordIDMap, method);
		return svm.classify(r.getFV());
	}
	
	public double p_sigmod(String str, int method) {
		ReviewDoc r = new ReviewDoc(str).setChi();
		r.setFeatureVector(this.wordIDMap, method);
		return 1/(1+Math.exp(-svm.classify(r.getFV())));
	}
	public double p_sigmod(String str, int method,String featureFilePath) {
		ReviewDoc r = new ReviewDoc(str).setChi();
		r.setFeatureVector(FileIO.readMapFile(featureFilePath), method);
		return 1/(1+Math.exp(-svm.classify(r.getFV())));
	}
}
