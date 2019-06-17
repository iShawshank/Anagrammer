package Anagrammer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import static Anagrammer.AnagramConstants.LIMIT_NOT_USED;

import static org.junit.Assert.*;

public class AnagramUtilsTest {

    AnagramUtils utils = new AnagramUtils();
    ArrayList<AnagramWord> anagramWordList = new ArrayList<AnagramWord>();

    @Before
    public void setUp() throws Exception {
        // Load anagramWordList
        anagramWordList.add(new AnagramWord("ready"));
        anagramWordList.add(new AnagramWord("deary"));
        anagramWordList.add(new AnagramWord("yeard"));
        anagramWordList.add(new AnagramWord("read"));
    }

    @After
    public void tearDown() throws Exception {


        // clear anagramWordList for next test.
        anagramWordList.clear();
    }

    @Test
    public void getAllAnagramsTestEmptyList() {
        ArrayList<String> actualAnagramList = new ArrayList<String>();
        ArrayList<String> expectedAnagramList = new ArrayList<String>();

        // retrieve actualAnagramList
        actualAnagramList = utils.getAllAnagrams(new AnagramWord("read"), LIMIT_NOT_USED, anagramWordList);

        // anagramList's size is 2
        assertTrue(actualAnagramList.size() == 0);


        // actualAnagramList and expectedAnagram list are equal
        assertTrue(actualAnagramList.equals(expectedAnagramList));
    }

    @Test
    public void getAllAnagramsTestWithoutLimit() {
        ArrayList<String> actualAnagramList = new ArrayList<String>();
        ArrayList<String> expectedAnagramList = new ArrayList<String>();

        // retrieve actualAnagramList
        actualAnagramList = utils.getAllAnagrams(new AnagramWord("ready"), LIMIT_NOT_USED, anagramWordList);

        // anagramList's size is 2
        assertTrue(actualAnagramList.size() == 2);

        // update expectedAnagramList
        expectedAnagramList.add("deary");
        expectedAnagramList.add("yeard");

        // actualAnagramList and expectedAnagram list are equal
        assertTrue(actualAnagramList.equals(expectedAnagramList));
    }

    @Test
    public void getAllAnagramsTestWithLimit() {
        ArrayList<String> actualAnagramList = new ArrayList<String>();
        ArrayList<String> expectedAnagramList = new ArrayList<String>();
        Integer limit = 1;

        // Load anagramWordList
        anagramWordList.add(new AnagramWord("ready"));
        anagramWordList.add(new AnagramWord("deary"));
        anagramWordList.add(new AnagramWord("yeard"));
        anagramWordList.add(new AnagramWord("read"));

        actualAnagramList = utils.getAllAnagrams(new AnagramWord("ready"), limit, anagramWordList);

        // anagramList's size is 2
        assertTrue(actualAnagramList.size() == 1);

        // update expectedAnagramList
        expectedAnagramList.add("deary");

        // actualAnagramList and expectedAnagram list are equal
        assertTrue(actualAnagramList.equals(expectedAnagramList));
    }

    @Test
    public void isAnagramTestisTrue() {
        AnagramWord wordOne = new AnagramWord("read");
        AnagramWord wordTwo = new AnagramWord("dear");

        // these words are anagrams of each other
        assertTrue(utils.isAnagram(wordOne, wordTwo));
    }

    @Test
    public void isAnagramTestisFalse() {
        AnagramWord wordOne = new AnagramWord("read");
        AnagramWord wordTwo = new AnagramWord("deary");

        // these words are NOT anagrams of each other
        assertTrue(!utils.isAnagram(wordOne, wordTwo));
    }

    @Test
    public void formatJsonArrayTest() {

        String expectedJsonString = "{\n" +
                "  \"anagrams\": [\n" +
                "    \"dare\",\n" +
                "    \"dear\"\n" +
                "  ]\n" +
                "}";
        String actualJsonString = "";
        ArrayList<String> list = new ArrayList<String>();

        // Load the arrayList for testing.
        list.add("dare");
        list.add("dear");

        // Get actual Json response
        actualJsonString = utils.formatJsonArray(list);

        // expectedJsonString and actualJsonString should be equal
        assertTrue(expectedJsonString.equals(actualJsonString));
    }

    @Test
    public void jsonToArrayListTest() {
        String jsonString = "{\"words\":[\"read\",\"dear\",\"dare\"]}";
        ArrayList<String> expectedList = new ArrayList<String>();
        ArrayList<String> actualList = new ArrayList<String>();

        // Load expected list
        expectedList.add("read");
        expectedList.add("dear");
        expectedList.add("dare");

        // load actual list
        actualList = utils.jsonToArrayList(jsonString);

        // actualList should contain 3 strings.
        assertTrue(actualList.size() == 3);

        // expectedList and actualList are equal
        assertTrue(expectedList.equals(actualList));
    }

    @Test
    public void wordIsInDataStoreTests() {
        String goodWord = "read";
        String failWord = "Kevin";

        // "read" is in data store
        assertTrue(utils.wordIsInDataStore(goodWord, anagramWordList));

        // "Kevin" is NOT in data store
        assertTrue(!utils.wordIsInDataStore(failWord, anagramWordList));
    }

    @Test
    public void removeWordFromDataStoreTest() {
        String wordToDelete = "ready";
        ArrayList<AnagramWord> expectedDataStore = new ArrayList<AnagramWord>();

        // load expectedDataStore
        expectedDataStore.add(new AnagramWord("deary"));
        expectedDataStore.add(new AnagramWord("yeard"));
        expectedDataStore.add(new AnagramWord("read"));

        // Before we delete ready, there should be 4 AnagramWords in the data store
        assertTrue(anagramWordList.size() == 4);

        // Delete "ready" in from data store.
        anagramWordList = utils.removeWordFromDataStore(wordToDelete, anagramWordList);

        // Should have 3 AnagramWords in the data store now.
        assertTrue(anagramWordList.size() == 3);

        // Data store and expectedDataStore are equal
        assertTrue(anagramWordList.equals(expectedDataStore));
    }

    @Test
    public void removeWordFromDataStoreTestWordDoesNotExist() {
        String wordToDelete = "readyz";
        ArrayList<AnagramWord> expectedDataStore = new ArrayList<AnagramWord>();

        // load expectedDataStore
        expectedDataStore.add(new AnagramWord("ready"));
        expectedDataStore.add(new AnagramWord("deary"));
        expectedDataStore.add(new AnagramWord("yeard"));
        expectedDataStore.add(new AnagramWord("read"));

        // Before we delete ready, there should be 4 AnagramWords in the data store
        assertTrue(anagramWordList.size() == 4);

        // Attempt to Delete "readyz" in from data store.
        anagramWordList = utils.removeWordFromDataStore(wordToDelete, anagramWordList);

        // Should have 3 AnagramWords in the data store now.
        assertTrue(anagramWordList.size() == 4);

        // Data store and expectedDataStore are equal
        assertTrue(anagramWordList.equals(expectedDataStore));
    }
}