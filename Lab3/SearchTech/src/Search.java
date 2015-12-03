

/**
 * Created by ethanmctiernan on 12/11/2015.
 */



import java.net.*;
import java.io.*;
import java.util.*;

class struct{

    String word;
    int frequency;
    double idf;
     double docfreq;

    void put(String wrd,int freq, double i_d_f,double df){
        word=wrd;
        frequency= freq;
        idf = i_d_f;
        docfreq = df;
    }
}

public class Search {

    static HashMap<String, LinkedList<struct>> docName_length = new HashMap<>();
    static LinkedList<String> searchTerms;

    static void downloadFile() throws IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        String searchStr="";
        for(int i= 0 ;i<searchTerms.size();i++) {
            if(i==searchTerms.size()-1) {
                searchStr = searchStr + searchTerms.get(i);
            }
            else
                searchStr = searchStr + searchTerms.get(i) + "%20";
            }
        String url = "http://136.206.115.117:8080/IRModelGenerator/SearchServlet?query=" +searchStr+     "&simf=BM25&k=1.2&b=0.75&numwanted=10";
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
    static void getWords() throws FileNotFoundException {
        String docName = " ";//Variable with the document name
        struct structure;
        String word;
        int frequency;
        double idf;
        boolean docNameRetrieved = false;
        boolean doneDoc = false;
        boolean docStart = false;
        Scanner reader = new Scanner(new FileInputStream("output.txt"));  //Read in the file
        LinkedList<struct> list= new LinkedList<>();
        while (reader.hasNext()) {      // while there is another token to read
            structure= new struct();
            String temp = reader.next(); //temp is equal to the next string
            if (!docNameRetrieved) {    //If we haven't yet read the document name
                String[] getDocStart = temp.split(">");  //split the string here
                if (getDocStart[0].equals("target=\"_blank\""))  //this is what the start of the string looks like
                {
                    String[] docHeading = getDocStart[1].split("<"); //if its a match split at the '<'
                    docName = docHeading[0];   //document name is equal to whats at position 0
                    docNameRetrieved = true;    //tell the program we have got the doc name
                    //System.out.println("DocName: " + docName);  //just for testing purposes
                }
            }
            if (temp.equals("Vector:") && !docStart) { //when we read the vector enter
                String s = reader.next();
                       //let the program know we have started reading the words in the document
                String[] wordSplit;
                wordSplit = s.split(">");
                String[] searchedWord = wordSplit[1].split(":");
                searchedWord[1] = searchedWord[1].substring(0, searchedWord[1].length() - 1);
                word = searchedWord[0];
                frequency = Integer.parseInt(searchedWord[1]);
                idf = Double.parseDouble(reader.next());
                double df = getDF(idf);
                structure.put(word, frequency, idf, df);
                list.add(structure);
                docStart = true;
                //System.out.println(searchedWord[0] + " " + searchedWord[1] + " " + idf);
            } else if (docStart) {
                //read the rest of the words in the document

                String[] wordSplit;
                wordSplit = temp.split(":");

                //System.out.println( " In loop 2 : "+wordSplit[0]);
                wordSplit[1] = wordSplit[1].substring(0, wordSplit[1].length() - 1);
                word = wordSplit[0];
                frequency = Integer.parseInt(wordSplit[1]);
                String s = reader.next();
                if (s.contains("<")) { //when a string contains the '<' we know we have reached the last word.
                    String[] lastEntry = s.split("<"); // split it and let s = to the idf
                    idf = Double.parseDouble(lastEntry[0]);
                    doneDoc = true;
                }
                else idf = Double.parseDouble(s);
                double df = getDF(idf);
                //System.out.println(wordSplit[0] + " " + wordSplit[1] + " " + s);
                structure.put(word,frequency,idf,df);
                //System.out.println(structure.word + structure.idf + structure.frequency);
                list.add(structure);
            }
            if (doneDoc) {
                doneDoc = false;
                docStart = false;
                docNameRetrieved = false;
                //System.out.println("List before its sent size " + list.size());
                //createList(docName, list);
                docName_length.put(docName, list);
                list = new LinkedList<>();
                //System.out.println("____________________________________________________");
            }



        }

        //System.out.println(docName_length);

    }

    static void printMap(){

        for ( String key: docName_length.keySet()){

            LinkedList <struct> fullList = docName_length.get(key);
            System.out.println(key);
            System.out.println(fullList.size());
            for (struct st : fullList) {
                System.out.println(st.word + " " + st.frequency + " " + st.idf + " " + st.docfreq);
            }


        }
    }

    static HashMap<String,Integer> wordDf = new HashMap<>();

    static void findWord(){

        int count = 0;
        for ( String key: docName_length.keySet()){

            LinkedList <struct> fullList = docName_length.get(key);
            for (struct st : fullList) {

                for( String k: docName_length.keySet()) {
                    LinkedList<struct> temp = docName_length.get(k);
                    {
                        for (struct s : temp) {
                            if(st.word.equals(s.word)){
                                count++;
                            }
                        }
                    }
                }
                wordDf.put(st.word,count);
                count = 0;
            }


        }

        for ( String key: wordDf.keySet()){

            int number = wordDf.get(key);
            robertson(key, number);


        }
    }

    static HashMap<String, Double> wordRob = new HashMap<>();

    static void robertson(String word, int ri){

            double ni=0.0;
        for (String key: docName_length.keySet()) {
            LinkedList<struct> fullList = docName_length.get(key);
            for (struct st : fullList) {
                if (st.word.equals(word)) {
                    ni = st.docfreq;
                }
            }
        }

            double top = (ri +0.5)*(500000 - ni - 10 + ri + 0.5);
            double bottom = (ni - ri +.5) * ( 10 - ri + 0.5);

            double result=Math.log(top/bottom);
        result = result*ri;
        wordRob.put(word, result);
        //System.out.println(word + " " + result);


    }
    static void getTop()
    {
        List<String> top5 = new ArrayList<String>();
        Map.Entry<String,Double> maxEntry = null;
        int count = 0;
        while(count<10)
        {
            maxEntry = null;
            for (Map.Entry<String, Double> entry : wordRob.entrySet())
            {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                {
                    maxEntry = entry;
                }
            }
            top5.add(maxEntry.getKey());
            wordRob.put(maxEntry.getKey(), 0.0);
            count++;
        }
        for (int i = 0; i < top5.size() ; i++) {

            searchTerms.add(top5.get(i));
        }
        System.out.println(searchTerms);
    }

    static void createRes(String name){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(name, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int count = 1;
        for(String key : docName_length.keySet()){
            writer.println("301\tQ0\t" + key +"\t" +count +"\t" + (1.0/count) + "\t" + "BM25.1.2.0.75");

            count++;

        }
        writer.close();


    }



    /*static void createList(String doc_name,LinkedList<struct> str) {
        struct test = str.get(0);
        docName_length.put(doc_name, str);
        System.out.println("size of map: "+docName_length.size());
        for ( String key: docName_length.keySet()){

            LinkedList <struct> fullList = docName_length.get(key);
            System.out.println("doucment name: " + key);
            System.out.println(fullList.size());
            //for (struct st : fullList) {
            //    System.out.println(st.word + " " + st.frequency + " " + st.idf);
            //}


        }

    }*/

    static double getDF(double idf){

        double docFreq = (1/idf) * 500000;
        return docFreq;
    }

        public static void main(String[] args) throws IOException {
            searchTerms = new LinkedList<>();
            searchTerms.add("International");
            searchTerms.add("Organized");
            searchTerms.add("Crime");
            downloadFile(); //commented it out because need to be on campus to access url
            try {
                getWords();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            findWord();

            createRes("1.1.res");
/////////////////////////////////////////////////////////

            getTop();
            downloadFile(); //commented it out because need to be on campus to access url
            try {
                getWords();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            findWord();
            createRes("1.2.res");
            //printMap();

        }

}
