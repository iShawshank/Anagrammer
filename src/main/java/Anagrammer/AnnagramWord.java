package Anagrammer;

public class AnnagramWord {

    private String word;
    private Integer letterCount;

    public AnnagramWord(String word) {
        this.word = word;
        this.letterCount = word.length();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getLetterCount() {
        return letterCount;
    }

    public void setLetterCount(Integer letterCount) {
        this.letterCount = letterCount;
    }
}
