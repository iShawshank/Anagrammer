package Anagrammer;

import java.util.Objects;

public class AnagramWord implements Comparable<AnagramWord>{

    private String word;
    private Integer letterCount;

    public AnagramWord(String word) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnagramWord that = (AnagramWord) o;
        return word.equals(that.word) &&
                letterCount.equals(that.letterCount);
    }

    // Used for comparing one anagram word with another
    @Override
    public int compareTo(AnagramWord o) {
        return this.getWord().compareTo(o.getWord());
    }

    @Override
    public String toString() {
        return "AnagramWord{" +
                "word='" + word + '\'' +
                '}';
    }
}
