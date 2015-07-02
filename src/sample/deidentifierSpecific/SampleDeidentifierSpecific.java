package sample.deidentifierSpecific;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import com.care.exception.ComponentException;
import com.care.framework.IDeIdentifier;

/**
 * This class is De-Identification Component for de-identifying a specific type of sem-formatted clinical documents.
 * @author Suryansh Raj 
 * @since 27-06-2015
 */

public class SampleDeidentifierSpecific implements IDeIdentifier
{
    @Override
    public List<String> DeIdentify(String data) throws ComponentException
    {
        String tokens[] = null;
        
        // Sample random names and years array. The totalRandoms variable is used in nextInt() method of Random class.
        String names[] = {"Random0", "Random1", "Random2", "Random3", "Random4", "Random5", "Random6", "Random7", "Random8", "Random9"};
        String years[] = {"1969", "1984", "2012", "2025", "1450", "2100", "2020", "1869", "2050", "1900"};
        int totalRandoms = 9;
        Random random = new Random();
        
        // yearOld stores the value of year mentioned in CRNO, yearNew stores the replaced value of year in CRNO
        int rand, start, end, yearOld = 2000, yearNew = 2050;
        
        InputStream is = null;
        try
        {
        	// Tokenization using OpenNLP Tokenizer Model and API
            String s =  URLDecoder.decode(getClass().getResource("en-token.bin").getFile().toString(), "UTF-8");
            File file = new File(s);
            System.out.println(file.exists());
            is = new FileInputStream(file);
            TokenizerModel modelTokenizer = null;
            modelTokenizer = new TokenizerModel(is);
            Tokenizer tokenizer = new TokenizerME(modelTokenizer);
            tokens = tokenizer.tokenize(data);
            
            // Name Recognition using OpenNLP Named Entity Recognition Model and API
            s =  URLDecoder.decode(getClass().getResource("en-ner-person.bin").getFile().toString(), "UTF-8");
            file = new File(s);
            System.out.println(file.exists());
            is = new FileInputStream(file);
            TokenNameFinderModel modelName = new TokenNameFinderModel(is);
            NameFinderME nameFinder = new NameFinderME(modelName);
            Span nameSpans[] = nameFinder.find(tokens);
            for (Span span : nameSpans) { 
            	start = span.getStart();
            	end = span.getEnd();
        		rand = random.nextInt(totalRandoms);
            	for (int k=start;k<end;k++) 
            		// The following condition is added since Discharge Type is being considered as a person name in this model.
            		if(!tokens[k].equalsIgnoreCase("Discharge")&&!tokens[k].equalsIgnoreCase("Type"))
            			tokens[k] = names[rand];
            }
            
            // Regular expressions
            Pattern crno = Pattern.compile("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
            Pattern date = Pattern.compile("[0-3]?[0-9][-/][0-1]?[0-9][-/][0-2][0-9][0-9][0-9]");
            Pattern unit = Pattern.compile("[Uu][Nn][Ii][Tt][-](\\d+)");
            Matcher match;
            
            for (int i=0; i<tokens.length; i++ ) {
            	
            	// CRNO modifications
            	if(tokens[i].equalsIgnoreCase("CRNO")||tokens[i].equalsIgnoreCase("CRNO:")) {
            		i++;
            		if(tokens[i].equalsIgnoreCase(":")) 
            			i++;
            		match = crno.matcher(tokens[i]);
            		if(match.find()) {
            			String yearExtract = tokens[i].substring(0, 4);
            			String restExtract = tokens[i].substring(4);
            			yearOld = Integer.parseInt(yearExtract);
            			do {
            				rand = random.nextInt(totalRandoms);
            				yearNew = Integer.parseInt(years[rand]);
            			} while (yearOld == yearNew);
            			yearExtract = Integer.toString(yearNew);
            			int restNumbers = Integer.parseInt(restExtract);
            			int digits[] = new int[6];
            			
            			// replacing the rest 6 digits by adding 5 to it and then taking the remainder upon division by 10            			
            			for (int j=5; j>=0; j--) {
            				digits[j] = restNumbers%10;
            				restNumbers = restNumbers/10;
            				digits[j] = (digits[j]+5) % 10;
            			}
            			
            			restExtract = Integer.toString(digits[0]);
            			for (int j=1; j<6; j++) 
            				restExtract = restExtract + Integer.toString(digits[j]);
            			tokens[i] = yearExtract + restExtract;
            		}
            		else
            			i--;
            	}
            	
            	// Admitted On Year and Discharged On Year modifications
            	else if(tokens[i].equalsIgnoreCase("Admitted")||tokens[i].equalsIgnoreCase("Discharged")) {
            		i++;
            		if(tokens[i].equalsIgnoreCase("on")||tokens[i].equalsIgnoreCase("on:")) {
            			i++;
            			if(tokens[i].equalsIgnoreCase(":")) 
                			i++;
            			match = date.matcher(tokens[i]);
                		if(match.find()) {
                			int j, length = tokens[i].length();
                			for( j=length-1; j>=0; j--) 
                				if(!Character.isDigit(tokens[i].charAt(j)))
                					break;
                			j++;
                			String restExtract = tokens[i].substring(0, j);
                			String yearExtract = tokens[i].substring(j);
                			int difference = Integer.parseInt(yearExtract);
                			difference = difference - yearOld;
                			difference = yearNew + difference;
                			yearExtract = Integer.toString(difference);
                			tokens[i] = restExtract + yearExtract;
                		}
            		}
            		else
            			i--;
            	}
            	
            	// Admission No modifications
            	else if(tokens[i].equalsIgnoreCase("Admission")) {
            		i++;
            		if(tokens[i].equalsIgnoreCase("no")||tokens[i].equalsIgnoreCase("no:")) {
            			i++;
            			if(tokens[i].equalsIgnoreCase(":")||tokens[i].equalsIgnoreCase(":")) 
                			i++;
            			if(tokens[i].equalsIgnoreCase(":")||tokens[i].equalsIgnoreCase(":")) 
                			i++;
            			int j;
            			for (j=0; Character.isLetterOrDigit(tokens[i].charAt(j)); j++);
            			j++;
            			String restExtract1 = tokens[i].substring(0,j);
            			String restExtract2 = tokens[i].substring(j+4);
            			String yearExtract = tokens[i].substring(j,j+4);
            			int difference = Integer.parseInt(yearExtract);
            			difference = difference - yearOld;
            			difference = yearNew + difference;
            			yearExtract = Integer.toString(difference);
            			tokens[i] = restExtract1 + yearExtract + restExtract2;
            		}
            		else
            			i--;
            	}
            	
            	// Special modification of Names in the Summary
            	else if(tokens[i].equalsIgnoreCase("Name")||tokens[i].equalsIgnoreCase("Consultant")) {
            		i++;
            		if(tokens[i].equalsIgnoreCase("Name"))
            			i++;
            		if(tokens[i].equalsIgnoreCase(":")) {
            			i++;
            			rand = random.nextInt(totalRandoms);
            			while(Character.isLetter(tokens[i].charAt(0))&&!tokens[i].equalsIgnoreCase("Age")&&!tokens[i].equalsIgnoreCase("Discharge")) 
            				tokens[i++] = names[rand];
            			i--;
            		}
            		else
            			i--;
            		
            	}
            	
            	// Unit modifications and Ward and Bed number removal.
            	else if(tokens[i].equalsIgnoreCase("Unit")) {
            		i++;
            		if(tokens[i].equalsIgnoreCase(":"))
            			i++;
            		match = unit.matcher(tokens[i]);
            		if(match.find()) {
            			String restExtract = tokens[i].substring(0,5);
            			tokens[i++] = restExtract + "XX";
            		}
            		while (!tokens[i].equalsIgnoreCase("Admission")&&!tokens[i].equalsIgnoreCase("Admitted")&&!tokens[i].equalsIgnoreCase("Discharged")) 
            			tokens[i++] = "__";
            		i--;
            	}
            	
            	// Address removal
            	else if(tokens[i].equalsIgnoreCase("Address")) {
            		i++;
            		if(tokens[i].equalsIgnoreCase(":")) {
            			i++;
            			while (!tokens[i].equalsIgnoreCase("Phone")) 
            				tokens[i++] = "__";
            			i--;
            		}
            		else 
            			i--;
            	}
            	
            	// Phone Number removal
            	else if(tokens[i].equalsIgnoreCase("Phone")) {
            		i++;
            		if(tokens[i].equalsIgnoreCase("No")||tokens[i].equalsIgnoreCase("No.")||tokens[i].equalsIgnoreCase("No:"))
            			i++;
            		if(tokens[i].equalsIgnoreCase("."))
            			i++;
            		if(tokens[i].equalsIgnoreCase(":"))
            			i++;
            		tokens[i] = "__";
            	}
            	
            }
            
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Arrays.asList(tokens);
    }

    @Override
    public List<String> DeIdentify(List<String> data) throws ComponentException
    {
        StringBuilder inputData = new StringBuilder();
        for(String s : data){
            inputData.append(s+" ");
        }
        String totalData = inputData.toString();
        return DeIdentify(totalData);
    }
}
