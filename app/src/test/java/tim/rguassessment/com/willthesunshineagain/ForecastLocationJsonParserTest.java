package tim.rguassessment.com.willthesunshineagain;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;
import tim.rguassessment.com.willthesunshineagain.utils.ForecastLocationsJsonParser;

@RunWith(JUnit4.class)
public class ForecastLocationJsonParserTest {

    @Test
    public void testForecastJson(){
        String siteList = TestDataFileReader.readTestDataFile();

        List<City> list = ForecastLocationsJsonParser.processJson(siteList);

        // Assert that list size is 6001
        junit.framework.TestCase.assertEquals(6001, list.size());
    }
}
