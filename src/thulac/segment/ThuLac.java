package thulac.segment;

import thulac.base.POCGraph;
import thulac.base.TaggedSentence;
import thulac.base.WordWithTag;
import thulac.character.CBTaggingDecoder;
import thulac.manage.*;

import java.io.IOException;

/**
 * Created by atone on 16/5/22.
 * ThuLac.java
 */
public class ThuLac {
    private static final String prefix = "resource/thulac_models/";

    private static POCGraph poc_cands;
    private static CBTaggingDecoder tagging_decoder;
    private static Preprocesser preprocesser;
    private static Postprocesser nsDict;
    private static Postprocesser idiomDict;
    private static Punctuation punctuation;
    private static TimeWord timeword;
    private static NegWord negword;
    private static VerbWord verbword;

    static {
        try {
            poc_cands = new POCGraph();

            tagging_decoder = new CBTaggingDecoder();
            tagging_decoder.threshold = 10000;
            tagging_decoder.separator = '/';
            tagging_decoder.init((prefix + "model_c_model.bin"), (prefix + "model_c_dat.bin"), (prefix + "model_c_label.txt"));
            tagging_decoder.setLabelTrans();

            preprocesser = new Preprocesser();
            preprocesser.setT2SMap((prefix + "t2s.dat"));

            nsDict = new Postprocesser((prefix + "ns.dat"), "ns", false);
            idiomDict = new Postprocesser((prefix + "idiom.dat"), "i", false);
            punctuation = new Punctuation((prefix + "singlepun.dat"));
            timeword = new TimeWord();
            negword = new NegWord((prefix + "neg.dat"));
            verbword = new VerbWord((prefix + "vM.dat"), (prefix + "vD.dat"));
        } catch (IOException e) {
            System.err.println("THULAC model init failed!");
            e.printStackTrace();
        }
    }

    public static String segment(String original) {
        if (original == null || original.length() == 0) {
            return "";
        }
        TaggedSentence tagged = new TaggedSentence();
        String[] lines = original.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String oiraw : lines) {
            String raw = preprocesser.clean(oiraw, poc_cands);
            if (raw.length() > 0) {
                tagging_decoder.segment(raw, poc_cands, tagged);
                nsDict.adjust(tagged);
                idiomDict.adjust(tagged);
                punctuation.adjust(tagged);
                timeword.adjustDouble(tagged);
                negword.adjust(tagged);
                verbword.adjust(tagged);

                for (WordWithTag word : tagged) {
                    sb.append(word.toString());
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String test = "性价比不错。电池也挺好的。就是屏幕有点小。";
        System.out.println(segment(test));
    }

}
