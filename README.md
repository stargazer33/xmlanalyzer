## Analysis
The [task description](https://agileengine.bitbucket.io/keFivpUlPMtzhfAy/) asks to find a "similar" element. 

The description of similarity is quite fuzzy. It is stated that `"...Any user can easily find this button visually..."`. 
On the other hand the task description say `"...No image/in-browser app analysis is needed. No CSS/JS analysis..."`.

It is well-known that with the help of CSS/JS the quite different HTML blocks can be modified to look similar.
If we ignore the cases where CSS/JS is applied - than the similarity between HTML elements can be
measured as STRING SIMILARITY a.k.a. STRING DISTANCE.
 
So the solution for the Smart XML Analyzer task is based on string similarity/string distance.  

## Design
We convert the original HTML element to some "standardized" string.
After this we convert all the HTML elements from the "sample" file to "standardized" strings.
Than we measure the string distances (using Levenstein algorithm or some other algorithm) between the 
original HTML element and all the elements in the "sample" file.
The element in the sample file with the minimal distance is the most "similar" one.

The [java string similarity](https://github.com/tdebatty/java-string-similarity) library used to measure
string distance. This library provides multiple similarity/string distance algorithms. This means we can
fine-tune this solution, if necessary.

## Prerequisites
* Java 1.8 or higher
 
## How to run the analyzer
Syntax:
```
java -jar xmlanalyzer-1.0-SNAPSHOT.jar <original_html_file> <sample_html_file> [original_html_element_id]
```
Arguments:
* original_html_file - original HTML file to find the element with attribute id=<original_html_element_id> and collect all the required information       
* sample_html_file - path to HTML file to search a similar element
* original_html_element_id - an optional HTML element ID. This element ID will be used to find the original HTML element in <original_html_file>. Default to "make-everything-ok-button" 

### Example 1:
```
java -jar xmlanalyzer-1.0-SNAPSHOT.jar src/test/resources/sample-0-origin.html src/test/resources/sample-4-the-mash.html
```
Console output of Example 1 (see the `SIMILAR HTML ELEMENT` at the end):
```
Original file: src/test/resources/sample-0-origin.html
Sample file: src/test/resources/sample-4-the-mash.html
Original HTML element ID (in origin file): make-everything-ok-button

ORIGINAL HTML ELEMENT (from src/test/resources/sample-0-origin.html ):
PATH TO THE ELEMENT:  html > body > div#wrapper > div#page-wrapper > div > div > div > div > a#make-everything-ok-button href="#ok"
THE ELEMENT: <a id="make-everything-ok-button" class="btn btn-success" href="#ok" title="Make-Button" rel="next" onclick="javascript:window.okDone(); return false;"> Make everything OK </a>

The minimal (normalized Levenshtein) distance between the original and similar elements: 0.24836601307189543

SIMILAR HTML ELEMENT (from src/test/resources/sample-4-the-mash.html):
PATH TO THE ELEMENT:  html > body > div#wrapper > div#page-wrapper > div > div > div > div > a href="#ok"
THE ELEMENT: <a class="btn btn-success" href="#ok" title="Make-Button" rel="next" onclick="javascript:window.okFinalize(); return false;"> Do all GREAT </a>
```


### Example 2:
```
java -jar xmlanalyzer-1.0-SNAPSHOT.jar src/test/resources/sample-0-origin.html src/test/resources/sample-4-the-mash.html make-everything-ok-button
```
