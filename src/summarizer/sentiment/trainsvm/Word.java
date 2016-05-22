package summarizer.sentiment.trainsvm;

public class Word {
	public String word;
	public int countPos;
	// public Integer truePos;
	public int countNeg;
	// public Integer trueNeg;
	public double A;
	public double B;
	public double C;
	public double D;
	public double Chi;

	public Word(String word, boolean cate) {
		this.word = word;
		if (cate) {
			countPos = 1;
			countNeg = 0;
		} else {
			countPos = 0;
			countNeg = 1;
		}

	}

	public Word getWord() {
		return this;
	}

	public Word update(boolean cate) {
		if (cate)
			countPos++;
		else
			countNeg++;
		return this;
	}

	public void setChi(int N, int truePos, int trueNeg) {
		A = 1.0 * this.countPos;
		B = 1.0 * this.countNeg;
		C = truePos - A;
		D = trueNeg - B;
		Chi = N * (A * D - B * C) * (A * D - B * C) / (A + C) / (A + B)
				/ (B + D) / (B + C);
	}
}
