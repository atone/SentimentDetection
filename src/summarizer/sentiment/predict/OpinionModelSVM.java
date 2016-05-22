package summarizer.sentiment.predict;

import jnisvmlight.FeatureVector;
import jnisvmlight.SVMLightModel;
import thulac.segment.ThuLac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author jhw
 * 
 */
class singleSVMModel {
	private String domain;
	private String aspect;
	private HashMap<String, Integer> features;
	private SVMLightModel model;

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

	public void setAspect(String aspect) {
		this.aspect = aspect;
	}

	public String getAspect() {
		return aspect;
	}

	public void setFeatures(HashMap<String, Integer> features) {
		this.features = features;
	}

	public HashMap<String, Integer> getFeatures() {
		return features;
	}

	public void setModel(SVMLightModel model) {
		this.model = model;
	}

	public SVMLightModel getModel() {
		return model;
	}

	@SuppressWarnings("deprecation")
	public singleSVMModel(String modelFilePath, String featureFilePath) {
		try {
			SVMLightModel model = SVMLightModel.readSVMLightModelFromURL(new File(modelFilePath).toURL());
			setModel(model);
			/*
			 * HashMap<String, Integer> hm = FileTool.LoadStrIntValFromFile(
			 * featureFilePath, 0, 0, 1, -1, "\\s+", Charset .forName("utf8"));
			 */
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new FileReader(
					featureFilePath));

			String line;
			while ((line = br.readLine()) != null) {
				String[] words = line.split("\\s+");
				// System.out.println(words[0]+":\t"+words[1]);
				hm.put(words[0], Integer.parseInt(words[1]));
			}

			br.close();
			setFeatures(hm);
			System.err.println(hm.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public int classify(FeatureVector fv) {
		int label = (model.classify(fv) > 0) ? 1 : -1;
		return label;
	}
}

public class OpinionModelSVM {

	private static final String ASPECT_PREFIX = "a";
	private static ArrayList<singleSVMModel> SVMModels;
	private static HashMap<String, singleSVMModel> DomAsptoSVMMap;

	// 读取手机领域 所有特征文件和模型文件
	static {
        // load models
        SVMModels = new ArrayList<singleSVMModel>();
        loadSingleSVMModel("手机", "./resource/svm");

        // load map
        DomAsptoSVMMap = new HashMap<String, singleSVMModel>();
        for (int i = 0; i < SVMModels.size(); i++) {
            singleSVMModel svml = SVMModels.get(i);
            DomAsptoSVMMap.put(svml.getDomain() + svml.getAspect(), svml);
            System.err.println(svml.getDomain() + svml.getAspect());
        }
	}

	public static void loadSingleSVMModel(String domain, String SVMModelFile) {
		File file = new File(SVMModelFile);
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.getName().contains(ASPECT_PREFIX)) {
                System.err.println(f.getAbsolutePath());
				singleSVMModel svml = new singleSVMModel(f.getAbsolutePath()
						+ "/model", f.getAbsolutePath() + "/feature");
				svml.setDomain(domain);
				svml.setAspect(f.getName());
				SVMModels.add(svml);
			}
		}
	}

	public static int predict(String splitedText, String aspect) {
		singleSVMModel svml = DomAsptoSVMMap.get("手机" + aspect);
		FeatureVector fv = getFeatureVector(svml, splitedText);
		return svml.classify(fv);
	}

	public static ArrayList<Integer> predict(List<String> splitedTextAL, String aspect) {
		ArrayList<Integer> labels = new ArrayList<Integer>();
		singleSVMModel svml = DomAsptoSVMMap.get("手机" + aspect);

		for (int i = 0; i < splitedTextAL.size(); i++) {
			FeatureVector fv = getFeatureVector(svml, splitedTextAL.get(i));
			labels.add(svml.classify(fv));
		}
		return labels;
	}

    private static List<String> splitStringToWordList(String splittedText) {
        String[] tokens = splittedText.split(" ");
        List<String> result = new ArrayList<String>();
        for (String token: tokens) {
            int idx = token.lastIndexOf('/');
            if (idx != -1) {
                result.add(token.substring(0, idx));
            } else {
                result.add(token);
            }
        }
        return result;
    }

	private static FeatureVector getFeatureVector(singleSVMModel svml,
			String splitedText) {
		List<String> words = splitStringToWordList(splitedText);
		HashMap<String, Integer> word2idMap = svml.getFeatures();
		HashMap<Integer, Integer> wordMap = new HashMap<Integer, Integer>();

		// DEBUG 验证feature里右边一栏数字无重复
		/*
		 * HashMap<Integer, Integer> wHash = new HashMap<Integer, Integer>();
		 * for(String wStr : word2idMap.keySet()){
		 * if(wHash.containsKey(word2idMap.get(wStr))) { int
		 * t=word2idMap.get(wStr); wHash.put(word2idMap.get(wStr),t+1);
		 * System.out.println("DENUG ZHEN TA MA SHUAI: "+wStr); } else{
		 * wHash.put(word2idMap.get(wStr),1); } }
		 */

		for (String w : words) {
			// debug
			if (!word2idMap.containsKey(w))
				continue;
			else {
//				 System.out.println("DEBUG: oms"+w+" "+
//				 word2idMap.get(w));
				wordMap.put(word2idMap.get(w), 1);
			}
		}

		int nDims = wordMap.size();
		int[] dims = new int[nDims];
		double[] values = new double[nDims];
		int j = 0;
		for (int wid : wordMap.keySet()) {
			// debug
			// System.out.println(wid);
			dims[j] = wid;
			values[j] = 1;
			j++;
		}
		FeatureVector fv = new FeatureVector(dims, values);
        return fv;
	}

    public static void main(String[] args) {
        System.out.println(predict(ThuLac.segment("屏幕不清晰"), "a3"));
        System.out.println(predict(ThuLac.segment("就是屏幕大了有些不习惯"), "a3"));
    }
}
