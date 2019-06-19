# Anagrammer

A simple Java REST API that allows fast searches for [anagrams](https://en.wikipedia.org/wiki/Anagram).

API Options:

- `POST /words.json`: Takes a JSON array of English-language words and adds them to the corpus (data store).
- `GET /anagrams/:word.json`:
  - Returns a JSON array of English-language words that are anagrams of the word passed in the URL.
  - This endpoint should support an optional query param that indicates the maximum number of results to return.
- `DELETE /words/:word.json`: Deletes a single word from the data store.
- `DELETE /words.json`: Deletes all contents of the data store.
- `GET /stats/stats.json`: Returns the total count of words in the data store and min/max/median/average word length.
- `DELETE /words/delete/:word.json`: Deletes a single word and all anagrams associated with that word from the data store.



## API Consumption Details:

Interact with the API over HTTP, all data sent and received is expected to be in JSON format

Example: (Note that the application currently serves over localhost port 3000):

```{bash}
# Adding words to the corpus
$ curl -i -X POST -d '{ "words": ["read", "dear", "dare"] }' http://localhost:3000/words.json
HTTP/1.1 201 Created
...

# Fetching anagrams
$ curl -i http://localhost:3000/anagrams/read.json
HTTP/1.1 200 OK
...
{
  "anagrams": [
    "dear",
    "dare"
  ]
}

# Specifying maximum number of anagrams
$ curl -i http://localhost:3000/anagrams/read.json?limit=1
HTTP/1.1 200 OK
...
{
  "anagrams": [
    "dare"
  ]
}

# Delete single word
$ curl -i -X DELETE http://localhost:3000/words/read.json
HTTP/1.1 204 No Content
...

# Delete all words
$ curl -i -X DELETE http://localhost:3000/words.json
HTTP/1.1 204 No Content
...

# Get Data Store Stats
$ curl -i http://localhost:3000/stats/stats.json
HTTP/1.1 200 OK
...
{
  "wordCount": 6,
  "median": 6.0,
  "min": 4,
  "max": 8
}

# Delete a word and all of it's anagrams 
$ curl -i -X DELETE http://localhost:3000/words/delete/read.json
HTTP/1.1 204 No Content
...
```

## Running Anagrammer Locally

First step is to clone / fork this repo.

You have 2 options for running the application locally:
1). First option is to just run the provided jar:
 Bash Example:
 - cd into the root of Anagrammer
 - run the following command `java -jar target/Anagrammer-1.0-1-task.jar`
 
2). Second Option is using an IDE (I recommend using IntelliJ IDEA)

Setup Instructions for IntelliJ IDEA:
- Import "New Project" into Intellij and select Anagrammer from the location you chose to clone this repo to.
- Select "Create Project from existing sources" and select "next".
- Select "Import project from external model", select "Maven" from the list, and select "next.
- Make sure that "Import Maven projects automatically" is selected and select "next".
- Make sure that "Shaw.Kevin.Anagrammer:1.0-1" is selected and select "next".
- Select your JDK version and select "next".
- Update the project's name / location if you wish and select "Finish".
- You should be able to build and run the application now.

We recommend that you use the Maven Tools Window to build the application so it will update the .jar file automatically.

To open the Maven Tools window:
1. Open a Maven Tool Window by navigating to `View -> Tool Windows -> Maven`. 
2. Inside the Maven Tool Window select `Annagrammer -> LifeCycle -> package` and click Run Maven Build to build the 
applcation and JAR.


## Test Suites:
There is 2 different test suites that have been implemented:

1). Public Endpoint test suite:
- Uses Ruby Test Unit to test the public endpoints.
- Found in /src/test/ruby/
- Must have Ruby installed ([docs](https://www.ruby-lang.org/en/documentation/installation/)):
- Uses the anagram_client.rb file in the same location.
- In order for these tests to be run the application must be running. 

To run the tests cd into `Anagrammer/src/test/ruby/`
```{bash}
ruby anagram_test.rb
```

2). Internal unit test suite:
- Uses Java JUnit to test internal AnagramUtils Class methods.
- Found in /src/test/java/
- Must have Java 8 JDK installed.

To run the tests in IntelliJ. 
1. Open a Maven Tool Window by navigating to `View -> Tool Windows -> Maven`. 
2. Inside the Maven Tool Window select `Annagrammer -> Lifecycle -> test` and click Run Maven Build 


## Design Overview:
TOOLS: 

I chose to use Spring and Maven for this project as Maven makes it easy to build / manage the application and
Spring makes it easy to implement / manage the REST API.

ENGLISH WORD DICTIONARY:

I chose to implement the dictionary as a class so that I could load and sort it on Application start. Spring 
auto loads the WordController on application start and it instantiates the Dictionary class. I added
the Load() and Store() methods to the constructor which enables this loading and sorting to happen.
When the application first starts, the dictionary.txt file is loaded into an ArrayList and then sorted. 
I chose this implementation as it only has to happens on the start of the application and the dictionary is only used 
when adding words to the data store. 

REST API DESIGN:

All public facing endpoints enter through a single controller found at `/src/main/java/Anagrammer/WordController.java`
There are numerous methods that assist the controller and those are all found in a helper class found at 
`/src/main/java/Anagrammer/AnagramUtils.java` I chose to add these helper methods to prevent duplicate code and I chose
to separate these methods from the controller to make it easier to read and to separate the implementation from the 
controller. This also allowed me to add unit tests to the helper methods which added a second layer of tests to the
application.

ENTITIES:
 
I chose to make an AnagramWord class to allow additional functionality to be added after MVP. I could've just used
Strings but wanted the ability to add additional features to the application in the future without a major refactor. An
example of this would be the check for Proper Nouns. I can add a property of isProperNoun to the AnagramWord and add a 
helper method to determine if an AnagramWord is a proper noun. This can be stored in the object instead of having to 
determine it every time you need to know if a word is a proper noun. 

I also chose to make a DataStoreStats class to make it easier to send it as a JSON object when called. This prevented
another helper method from being created. 

The last class I created was an AnagramConstants class to allow a single storage for Application constants. 


## Future features:

The following is a list of potential features that could be valuable:
- Respect a query param for whether or not to include proper nouns in the list of anagrams.
- Endpoint that identifies words with the most anagrams.
- Endpoint that takes a set of words and returns whether or not they are all anagrams of each other.
- Endpoint to return all anagram groups of size >= *x*.
- Endpoint that returns all words in the data store currently.

