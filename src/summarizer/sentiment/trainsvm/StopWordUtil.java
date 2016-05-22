package summarizer.sentiment.trainsvm;

import java.util.HashSet;

public class StopWordUtil {
	private static HashSet<String> stopWordSet = null;
	static {
		stopWordSet = FileIO.readStopWordList("./resource/stopwords.txt");
	}

	public static boolean isStopWord(String word) {
		return stopWordSet.contains(word);
	}
}
