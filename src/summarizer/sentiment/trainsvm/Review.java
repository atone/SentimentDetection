package summarizer.sentiment.trainsvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author wyq
 *
 */
public class Review {

	public boolean category;
	private ArrayList<ReviewDoc> reviewDoc;

	/**
	 * @param r1
	 * @param r2
	 * @param cate
	 * @return
	 */
	public static Review mergeReview(Review r1, Review r2, boolean cate) {
		Review r3 = new Review(cate);
		ArrayList<ReviewDoc> r3_str = new ArrayList<ReviewDoc>();
		for (int i = 0; i < r1.getReviewDoc().size(); i++) {
			r3_str.add(r1.getReviewDoc().get(i));
		}
		for (int i = 0; i < r2.getReviewDoc().size(); i++) {
			r3_str.add(r2.getReviewDoc().get(i));
		}
		r3.setReview(r3_str);
		return r3;
	}

	private void setReview(ArrayList<ReviewDoc> r) {
		this.reviewDoc = r;
	}

	public Review(boolean cate) {
		this.category = cate;
		this.reviewDoc = new ArrayList<ReviewDoc>();
	}

	public void SetCatagory(boolean category) {
		this.category = category;
	}

	public ArrayList<ReviewDoc> getReviewDoc() {
		return this.reviewDoc;
	}

	public void build(ArrayList<String> input) {
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i) != null && input.get(i).length() > 0) {
				this.reviewDoc.add(new ReviewDoc(input.get(i)).setChi());
			}
		}
		System.out.println("Trained corpus length " + this.reviewDoc.size()
				+ " .");
	}
	

	/**
	 * @param allWords
	 */
	public void updateAllWords(HashMap<String, Word> allWords) {
		for (int i = 0; i < this.reviewDoc.size(); i++) {
			Iterator<Entry<String, Integer>> iter = this.reviewDoc.get(i)
					.getWordMap().entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Integer> entry = iter.next();
				if (allWords.containsKey(entry.getKey())) {
					allWords.put(entry.getKey(), allWords.get(entry.getKey())
							.update(this.category));
				} else {
					allWords.put(entry.getKey(), new Word(entry.getKey(),
							this.category));
				}
//				System.out.println(entry.getKey() + " : " + entry.getKey());
			}
		}
	}
}
