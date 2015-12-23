package serverPack;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorMatcher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import serverPack.coreclassess.Converter;
import serverPack.coreclassess.FeaturesORB;
import serverPack.coreclassess.SimilarityIndex;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;

@RestController
@RequestMapping(value = "/myservice")
@ComponentScan("serverPack")
public class MyRestService {

	private static final LinkedList<URL> posterUrls = new LinkedList<URL>();
	private static final LinkedList<String> posterNames = new LinkedList<String>();
	private static final LinkedList<Mat> posters_TrainDescriptors = new LinkedList<Mat>();
	private LinkedList<URL> bestURLS = new LinkedList<URL>();
	private LinkedList<String> bestNames = new LinkedList<String>();
	private LinkedList<LinkedList<String>> IMDBDetials = new LinkedList<LinkedList<String>>();
	
	private DescriptorMatcher descriptorMatcher;
	private FeaturesORB featuresORB;
	private Mat queryDescriptor;
	private Mat trainDescriptor;
	private MatOfDMatch matches;

	/////////////////////////////// [START] FOR MULTIPART FORM DATA... IF REQUIRED ////////////////////////////////
	
/*	@RequestMapping(value = "/upload", method=RequestMethod.POST)
	public @ResponseBody ResponseModel uploadFile(@RequestParam("uploadedFile") MultipartFile uploadedFileRef) {

		Loader.init();
		
		FeaturesORB orb = new FeaturesORB();
		queryDescriptor = new Mat();
		matches = new MatOfDMatch();
		List<SimilarityIndex> similarIndices = new ArrayList<SimilarityIndex>();
		
		try {
			InputStream inS = uploadedFileRef.getInputStream();
			descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
			queryDescriptor = orb.getORBFeaturesDescriptorMat(Converter.img2Mat(ImageIO.read(inS)));

			for (int i = 0; i < posters_TrainDescriptors.size(); i++) {
				descriptorMatcher.clear();			
				trainDescriptor = posters_TrainDescriptors.get(i);				
				descriptorMatcher.match(queryDescriptor, trainDescriptor, matches);

				List<DMatch> matchesList = matches.toList();

				Double max_dist = 0.0;
				Double min_dist = 100.0;

				for (int j = 0; j < queryDescriptor.rows(); j++) {
					Double dist = (double) matchesList.get(j).distance;
					if (dist < min_dist)
						min_dist = dist;
					if (dist > max_dist)
						max_dist = dist;
				}

				// -- Draw only "good" matches
				
				LinkedList<DMatch> good_matches = new LinkedList<>();
				double goodMatchesSum = 0.0;

				// good match = distance > 2*min_distance ==> put them in a list
				for (int k = 0; k < queryDescriptor.rows(); k++) {
					if (matchesList.get(k).distance < Math.max(2 * min_dist,0.02)) {
						good_matches.addLast(matchesList.get(k));
						goodMatchesSum += matchesList.get(k).distance;
					}
				}

				double simIndex = (double) goodMatchesSum/ (double) good_matches.size();
				similarIndices.add(new SimilarityIndex(simIndex, posterUrls.get(i), posterNames.get(i)));
			}
			
			
			/////////sorting/////////////
			Comparator<SimilarityIndex> indexComparator = new Comparator<SimilarityIndex>() {
			    public int compare(SimilarityIndex index1, SimilarityIndex index2) {
			        return index1.getIndex().compareTo(index2.getIndex());
			    }
			};
			
			Collections.sort(similarIndices, indexComparator);
			///////ending/////////////
					
			
			////////////BestOptions adding////////////
			
			bestURLS.clear();
			bestNames.clear();
			IMDBDetials.clear();
			for(int i=0; i<5 ; i++)
			{
				bestNames.add(i, similarIndices.get(i).getName());
				bestURLS.add(i, similarIndices.get(i).getUrl());
				IMDBDetials.add(i, getImdbData(similarIndices.get(i).getName()));				
			}
			
			////////////////end best option getting///////////
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseModel(bestNames, bestURLS, IMDBDetials);
	}*/
	
/////////////////////////////// [STOP] FOR MULTIPART FORM DATA... IF REQUIRED ////////////////////////////////
	
	
	
/////////////////////////////// [SART] FOR IPhone ////////////////////////////////	
	
	@RequestMapping(value = "/uploadme", method=RequestMethod.POST)
    public @ResponseBody ResponseModel uploadObject4(@RequestBody FlickwizImage fi) {
        System.out.println("Request Received on path /uploadme");
        System.out.println(fi);
        Loader.init();
		
		FeaturesORB orb = new FeaturesORB();
		queryDescriptor = new Mat();
		matches = new MatOfDMatch();
		List<SimilarityIndex> similarIndices = new ArrayList<SimilarityIndex>();
		
        try{
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(fi.getBase64Code());
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
           
            
			descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
			queryDescriptor = orb.getORBFeaturesDescriptorMat(Converter.img2Mat(img));

			for (int i = 0; i < posters_TrainDescriptors.size(); i++) {
				descriptorMatcher.clear();			
				trainDescriptor = posters_TrainDescriptors.get(i);				
				descriptorMatcher.match(queryDescriptor, trainDescriptor, matches);

				List<DMatch> matchesList = matches.toList();

				Double max_dist = 0.0;
				Double min_dist = 100.0;

				for (int j = 0; j < queryDescriptor.rows(); j++) {
					Double dist = (double) matchesList.get(j).distance;
					if (dist < min_dist)
						min_dist = dist;
					if (dist > max_dist)
						max_dist = dist;
				}

				// -- Draw only "good" matches
				
				LinkedList<DMatch> good_matches = new LinkedList<>();
				double goodMatchesSum = 0.0;

				// good match = distance > 2*min_distance ==> put them in a list
				for (int k = 0; k < queryDescriptor.rows(); k++) {
					if (matchesList.get(k).distance < Math.max(2 * min_dist,0.02)) {
						good_matches.addLast(matchesList.get(k));
						goodMatchesSum += matchesList.get(k).distance;
					}
				}

				double simIndex = (double) goodMatchesSum/ (double) good_matches.size();
				similarIndices.add(new SimilarityIndex(simIndex, posterUrls.get(i), posterNames.get(i)));
			}
			
			
			/////////sorting/////////////
			Comparator<SimilarityIndex> indexComparator = new Comparator<SimilarityIndex>() {
			    public int compare(SimilarityIndex index1, SimilarityIndex index2) {
			        return index1.getIndex().compareTo(index2.getIndex());
			    }
			};
			
			Collections.sort(similarIndices, indexComparator);
			///////ending/////////////
					
			
			////////////BestOptions adding////////////
			
			bestURLS.clear();
			bestNames.clear();
			IMDBDetials.clear();
			for(int i=0; i<5 ; i++)
			{
				bestNames.add(i, similarIndices.get(i).getName());
				bestURLS.add(i, similarIndices.get(i).getUrl());
				IMDBDetials.add(i, getImdbData(similarIndices.get(i).getName()));				
			}
			
			////////////////end best option getting///////////
            
        }catch(Exception ex){
            System.out.println("Base64Code to bufferedImage conversion exception");
            System.out.println(ex.getMessage());
        }
        return new ResponseModel(bestNames, bestURLS, IMDBDetials);
    }
	
////////////////////////////////////// [END] FOR IPhone ////////////////////////////////	
	
	private LinkedList<String> getImdbData(String movie)
	{
		final LinkedList<String> dataIMDB = new LinkedList<>();
		dataIMDB.clear();
		try {
		
			InputStream input = new URL("http://www.omdbapi.com/?t=" + URLEncoder.encode(movie, "UTF-8")).openStream();
			Map<String, String> map=new Gson().fromJson(new InputStreamReader(input, "UTF-8"), new TypeToken<Map<String, String>>(){}.getType());
					
			      
	        dataIMDB.add(map.get("Title"));
	        dataIMDB.add(map.get("Year"));
	        dataIMDB.add(map.get("Released"));
	        dataIMDB.add(map.get("Runtime"));
	        dataIMDB.add(map.get("Genre"));
	        dataIMDB.add(map.get("Actors"));
	        dataIMDB.add(map.get("Plot"));
	        dataIMDB.add(map.get("imdbRating"));
		} catch (Exception e) {
			
			System.out.println(e.getMessage().toString());
		}
		return dataIMDB;
	}

	
	@RequestMapping(value = "/calcallfeatures", method=RequestMethod.GET)
	public @ResponseBody String allFeaturesExtraction() throws IOException{
		int counter = 0;
		Loader.init();
		featuresORB = new FeaturesORB();
		String[] nextLine;
		// String checkString = new String();
		CSVReader reader = new CSVReader(new FileReader("movieFile/movies.csv"), ',', '\'', 1);
		while ((nextLine = reader.readNext()) != null) {
			// nextLine[] is an array of values from the line
			String imageUrl = (String.valueOf(nextLine[1].charAt(0)).equals(
					"\"") ? nextLine[1].substring(1, nextLine[1].length() - 1)
					: nextLine[1]);

			String imageName = (String.valueOf(nextLine[0].charAt(0)).equals(
					"\"") ? nextLine[0].substring(1, nextLine[0].length() - 1)
					: nextLine[0]);



			posters_TrainDescriptors.add(counter, featuresORB
					.getORBFeaturesDescriptorMat(Converter.img2Mat(ImageIO
							.read(new URL(imageUrl)))));

			posterNames.add(counter, imageName);
			posterUrls.add(counter, new URL(imageUrl));

			++counter;

		}
		reader.close();
		return "Features extracted from all posters and stored successfully:"+posters_TrainDescriptors.size();
	}
	
}
