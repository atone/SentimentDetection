package summarizer.sentiment.trainsvm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class SortUtil {

	private static Comparator<Word> comparator = new Comparator<Word>() {

		@Override
		public int compare(Word o1, Word o2) {
			return Double.compare(o2.Chi, o1.Chi);
		}
		
	};
	
	private static Comparator<Entry<String, Double>> comparator2 = new Comparator<Map.Entry<String,Double>>() {

		@Override
		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
		
	};
	
	public static void sortWord(ArrayList<Word> words) {
		Collections.sort(words, comparator);
	}
	
	public static void sortEntry(ArrayList<Entry<String, Double>> entries) {
		Collections.sort(entries, comparator2);
	}
}
