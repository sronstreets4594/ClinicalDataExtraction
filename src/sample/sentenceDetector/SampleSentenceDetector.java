package sample.sentenceDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.tokenize.*;
import opennlp.tools.sentdetect.*;
import opennlp.tools.namefind.*;

import com.care.exception.ComponentException;
import com.care.framework.ITokenizer;

/**
 * Created by AMIT on 7/11/14.
 */
public class SampleSentenceDetector implements ITokenizer 
{
    @Override
    public List<String> Tokenize(String data) throws ComponentException
    {
        String sentences[] = null;
        InputStream is = null;
        try
        {
            String s =  URLDecoder.decode(getClass().getResource("en-sent.bin").getFile().toString(), "UTF-8");
            File file = new File(s);
            System.out.println(file.exists());
            is = new FileInputStream(file);
            SentenceModel modelSentence = new SentenceModel(is);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(modelSentence);
            sentences = sentenceDetector.sentDetect(data);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Arrays.asList(sentences);
    }

    @Override
    public List<String> Tokenize(List<String> data) throws ComponentException
    {
        StringBuilder inputData = new StringBuilder();
        for(String s : data){
            inputData.append(s+" ");
        }
        
        String sentences[] = null;
        InputStream is = null;
        try
        {
            String s =  URLDecoder.decode(getClass().getResource("en-sent.bin").getFile().toString(), "UTF-8");
            File file = new File(s);
            System.out.println(file.exists());
            is = new FileInputStream(file);
            SentenceModel modelSentence = new SentenceModel(is);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(modelSentence);
            sentences = sentenceDetector.sentDetect(inputData.toString());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Arrays.asList(sentences);
    }
}
