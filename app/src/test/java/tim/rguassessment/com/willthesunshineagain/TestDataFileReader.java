package tim.rguassessment.com.willthesunshineagain;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

public class TestDataFileReader {

    /**
     * Reads test data from a file, located at app/src/test/resources/sitelist.json
     * and returns the contents as a String.
     * @return The content of sitelist.json file, or null if
     */
    public static String readTestDataFile(){
        // Create an input stream to the file in resources/sitelist.json
        InputStream inputStream = Objects.requireNonNull(
                TestDataFileReader.class.getClassLoader().getResourceAsStream("sitelist.json"));

        // check that the stream has been created
        assert inputStream != null;

        // open a Scanner on the input stream until the end of the stream
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");

        // if s.hasNext() returns true, some text was read from the stream,
        if (s.hasNext()){
            // return the text that was read from the stream
            return s.next();
        } else {
            // nothing was read from the stream, so return null.
            return null;
        }
    }

}
