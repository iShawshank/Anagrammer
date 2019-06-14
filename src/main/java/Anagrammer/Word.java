package Anagrammer;

public class Word {

    private String word;
    private Integer letterCount;

    public Word(String word) {
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
