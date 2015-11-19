

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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        int count=0;
        boolean doneDoc = false;
        boolean docStart = false;
        Scanner reader = new Scanner(new FileInputStream("output.txt"));
        while (reader.hasNext()) {      // while there is another token to read
            String temp = reader.next();
             if (temp.equals("Vector:") && !docStart){
                String s = reader.next();
                 docStart = true;
                 String[] wordSplit;
                wordSplit= s.split(">");
                 String [] searchedWord=wordSplit[1].split(":");
                 searchedWord[1] = searchedWord[1].substring(0, searchedWord[1].length()-1);
                 s = reader.next();
                 System.out.println(searchedWord[0] +" " + searchedWord[1] + " " +s);
            }
            else if(docStart){


                 String[] wordSplit;
                 wordSplit = temp.split(":");

                 //System.out.println( " In loop 2 : "+wordSplit[0]);
                 wordSplit[1] = wordSplit[1].substring(0, wordSplit[1].length()-1);
                 String s = reader.next();
                 if(s.contains("<")) {
                     String[] lastEntry = s.split("<");
                     s = lastEntry[0];
                     doneDoc = true;
                 }
                 System.out.println(wordSplit[0] + " " + wordSplit[1]+ " "+s);
            }
            if (doneDoc) {doneDoc=false;docStart = false; System.out.println("____________________________________________________");}


        }

    }

        public static void main(String[] args) throws Exception {

            //downloadFile();
            getWords();

    }

}
