package Anagrammer;

public class DataStoreStats {

    private Integer wordCount;
    private Double median;
    private  Integer min;
    private Integer max;

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "DataStoreStats{" +
                "wordCount=" + wordCount +
                ", median=" + median +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
