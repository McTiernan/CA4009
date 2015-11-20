

/**
 * Created by ethanmctiernan on 12/11/2015.
 */


import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Search {

    static void downloadFile() throws IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        String url = "http://136.206.115.117:8080/IRModelGenerator/SearchServlet?query=dai&simf=BM25&k=1.2&b=0.75&numwanted=10";
        try {
            in = new BufferedInputStream(new URL(url).openStream());
            fout = new FileOutputStream("output.txt");

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
    //HashMap < String , int> words = new HashMap();
    static void getWords() throws FileNotFoundException {
        String docName;                                     //Variable with the document name
        boolean docNameRetrieved =false;
        boolean doneDoc = false;
        boolean docStart = false;
        Scanner reader = new Scanner(new FileInputStream("output.txt"));  //Read in the file
        while (reader.hasNext()) {      // while there is another token to read
            String temp = reader.next(); //temp is equal to the next string
            if (!docNameRetrieved ){    //If we haven't yet read the document name
                String [] getDocStart = temp.split(">");  //split the string here
                if(getDocStart[0].equals("target=\"_blank\""))  //this is what the start of the string looks like
                {
                    String[] docHeading = getDocStart[1].split("<"); //if its a match split at the '<'
                    docName = docHeading[0];   //document name is equal to whats at position 0
                    docNameRetrieved = true;    //tell the program we have got the doc name
                    System.out.println("DocName: " + docName);  //just for testing purposes
                }
            }
             if (temp.equals("Vector:") && !docStart){ //when we read the vector enter
                String s = reader.next();
                 docStart = true;       //let the program know we have started reading the words in the document
                 String[] wordSplit;
                wordSplit= s.split(">");
                 String [] searchedWord=wordSplit[1].split(":");
                 searchedWord[1] = searchedWord[1].substring(0, searchedWord[1].length()-1);
                 s = reader.next();
                 System.out.println(searchedWord[0] + " " + searchedWord[1] + " " +s);
            }
            else if(docStart){
                    //read the rest of the words in the document

                 String[] wordSplit;
                 wordSplit = temp.split(":");

                 //System.out.println( " In loop 2 : "+wordSplit[0]);
                 wordSplit[1] = wordSplit[1].substring(0, wordSplit[1].length()-1);
                 String s = reader.next();
                 if(s.contains("<")) { //when a string contatins the '<' we know we have reached the last word.
                     String[] lastEntry = s.split("<"); // split it and let s = to the idf
                     s = lastEntry[0];
                     doneDoc = true;
                 }
                 System.out.println(wordSplit[0] + " " + wordSplit[1]+ " "+s);
            }
            if (doneDoc) {doneDoc=false;docStart = false; docNameRetrieved = false; System.out.println("____________________________________________________");}


        }

    }

        public static void main(String[] args) throws Exception {

            //downloadFile(); commented it out because need to be on campus to access url
            getWords();

    }

}
