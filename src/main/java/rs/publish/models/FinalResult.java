package rs.publish.models;

import java.util.Dictionary;
import java.util.List;

public class FinalResult
{
	    private int firstResult;
	    private int maxResultHits;
	    private int totalHits;
	    private List<Dictionary<String, String>> hits;
	    
	    public int getFirstResult() {
			return firstResult;
		}
		public void setFirstResult(int firstResult) {
			this.firstResult = firstResult;
		}
		public int getMaxResultHits() {
			return maxResultHits;
		}
		public void setMaxResultHits(int maxResultHits) {
			this.maxResultHits = maxResultHits;
		}
		public int getTotalHits() {
			return totalHits;
		}
		public void setTotalHits(int totalHits) {
			this.totalHits = totalHits;
		}
		public List<Dictionary<String, String>> getHits() {
			return hits;
		}
		public void setHits(List<Dictionary<String, String>> hits) {
			this.hits = hits;
		}
}
