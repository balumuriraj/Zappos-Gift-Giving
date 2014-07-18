package com.zappos.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.zappos.model.JsonFacetObj;
import com.zappos.model.JsonFacetObj.Values;
import com.zappos.model.JsonObj;
import com.zappos.model.UserInput;
import com.zappos.model.JsonObj.Results;

//This the core part of the application the handles the requests and redirects to appropriate views.
@Controller
public class MainController {

	//The function redirects to the index page which requires input from the users.
	@RequestMapping(value ="/", method = RequestMethod.GET)
	public  ModelAndView showpage() {
		ModelAndView modelAndView = new ModelAndView("index");
		modelAndView.addObject("desired", new UserInput());
		return modelAndView;
	}

	//This function is used to compute the results for the user's input.
	@RequestMapping(value ="/", method = RequestMethod.POST)
	public  ModelAndView getresults(@ModelAttribute("desired") UserInput desired, BindingResult result) throws JsonParseException, JsonMappingException, RestClientException, IOException, HttpClientErrorException  {

		//Validation of user input begins here.
		boolean validate = true;
		String error1 = null; 
		String error2 = null;

		//validates input 1 that is the number of products
		if(desired.getProductcount() <= 0){
			System.out.println("Please enter a number greater than 0!");
			error1 = "Please enter a number greater than 0!";
			validate = false;
		}

		//validates input 2 that is the desired total amount
		if(desired.getAmount() <= 1){
			System.out.println("Please enter an amount greater than $1.00!");
			error2 = "Please enter an amount greater than $1.00!";
			validate = false;
		}

		//Displays errors to the user if any. 
		if(!validate){
			ModelAndView modelAndView = new ModelAndView("index");
			modelAndView.addObject("error1", error1);
			modelAndView.addObject("error2", error2);
			return modelAndView;

		}
		//Validation of user input ends here.

		System.out.println("No. of products: " + desired.getProductcount());
		System.out.println("Total Amount: " + desired.getAmount());

		//computes the average cost of a product
		Double averagecost = desired.getAmount()/desired.getProductcount();
		System.out.println("Average cost of a product: " + averagecost);

		//Zappos API call begins here
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();

		//parameters of the API call using resttemplate
		String apikey = "a73121520492f88dc3d33daf2103d7574f1a3166";
		String facetexcludeterms = "\"facetField\",\"facetFieldDisplayName\",\"results\",\"limit\",\"originalTerm\",\"currentResultCount\",\"totalResultCount\",\"filters\"";
		String filter;

		//This filter is used to narrow down the results based on the product average cost.
		if(averagecost <= 50){
			filter = "{\"priceFacet\":[\"$50.00 and Under\"]}";
		} else if(averagecost > 50 && averagecost <= 100){
			filter = "{\"priceFacet\":[\"$100.00 and Under\"]}";
		} else if(averagecost > 100 && averagecost <= 200){
			filter = "{\"priceFacet\":[\"$200.00 and Under\"]}";
		} else{
			filter = "{\"priceFacet\":[\"$200.00 and Over\"]}";
		}

		JsonFacetObj facetobj = new JsonFacetObj();

		try{
			//API call that returns a Json object that consists of counts of the products of a particular price.
			facetobj = mapper.readValue(restTemplate.getForObject("http://api.zappos.com/Search?limit=100&excludes=[{facetexcludeterms}]&includes=[\"facets\"]&facets=[\"price\"]&filters={filter}&facetSort=name&key={apikey}", String.class, facetexcludeterms, filter, apikey), JsonFacetObj.class);
		}
		catch(HttpClientErrorException e){
			ModelAndView mv = new ModelAndView("index");
			mv.addObject("error", "Oops! Something went wrong. Please try again!");
			return mv;
		}

		//Zappos API call ends here

		System.out.println(facetobj.getStatusCode());

		//Displays error to the user if the call fails due to some reason.
		if(!facetobj.getStatusCode().equals("200")){
			ModelAndView mv = new ModelAndView("index");
			mv.addObject("error", facetobj.getError());
			return mv;
		}

		ArrayList<Values> facetvalues = facetobj.getFacets().get(0).getValues(); //the objects containing the counts of products of a particular price are stored in an array list
		HashMap<Double, Integer> facethash = new HashMap<Double, Integer>();
		ArrayList<Double> searchlist = new ArrayList<Double>();
		Iterator<Values> it = facetvalues.iterator();

		//the objects copied to a hash map with cost as key and count as value. The costs are also copied into an array list which will be used later 
		while(it.hasNext()){
			Values temp = it.next();
			facethash.put(Double.parseDouble(temp.getName()), temp.getCount());
			searchlist.add(Double.parseDouble(temp.getName()));
		}

		int index = searchlist(averagecost, searchlist); //This function is used to retrieve the index of cost that is similar to the user's average product cost
		System.out.println("index is " + index);
		System.out.println(searchlist.get(index));

		int count = facethash.get(searchlist.get(index));
		System.out.println("Count of " + searchlist.get(index) + " is " + count);

		ArrayList<Results> list = new ArrayList<Results>();
		ArrayList<HashMap<Results[], Double>> combos = new ArrayList<HashMap<Results[], Double>>();

		//This function gives the list of products without duplicates and are of cost similar to user's input that is retrieved above
		list = productlist(averagecost, index, searchlist, desired, list); 

		if(list == null){
			ModelAndView mv = new ModelAndView("index");
			mv.addObject("error", "Something went wrong. Please try again!");
			return mv;
		}

		int downindex = index;
		int upindex = index;
		boolean godown = true;

		//If the list generated above do not consists of sufficient number of products to create 5 combos, it will again call the api to get the next below desired price and next above desired price alternatively until the combo list is complete.  
		while(list.size() < 5*desired.getProductcount()){

			//This will give the list of products with next below desired cost
			if(godown){
				if(downindex != 0){
					downindex--;
					list = productlist(averagecost, downindex, searchlist, desired, list);
					if(list == null){
						ModelAndView mv = new ModelAndView("index");
						mv.addObject("error", "Something went wrong. Please try again!");
						return mv;
					}
				} 
				godown = false;
			} 

			//This will give the list of products with next above desired cost
			else{

				if(upindex != searchlist.size()-1){
					upindex++;
					list = productlist(averagecost, upindex, searchlist, desired, list);
					if(list == null){
						ModelAndView mv = new ModelAndView("index");
						mv.addObject("error", "Something went wrong. Please try again!");
						return mv;
					}
				}
				godown = true;
			}
		}

		//This will generate 5 combos from the list generated above.
		combos = combolist(list, desired, combos);		

		//Displays the combos to the user.
		ModelAndView modelAndView = new ModelAndView("index");
		modelAndView.addObject("combos", combos);
		return modelAndView;

	}

	//This function is used to retrieve the cost that is similar to the user's average product cost
	public int searchlist(double num, ArrayList<Double> list){
		double temp;
		Iterator<Double> itr = list.iterator();
		int index = 0;

		//This loop will check if the user's desired price exists in the list. If not present, it returns the cost below user's desired cost 
		while(itr.hasNext()){
			temp = (double) itr.next();

			if(num == temp){
				return index;
			}
			else if(temp > num){
				break;
			} 
			index++;
		}

		if(index != 0)
			return index-1;
		else
			return 0;

	}

	//This function gives the list of products which are of cost similar to user's input that is retrieved above
	public ArrayList<Results> productlist(double averagecost, int index, ArrayList<Double> searchlist, UserInput desired, ArrayList<Results> list) throws JsonParseException, JsonMappingException, RestClientException, IOException, HttpClientErrorException {

		//parameters of the API call to get product list
		String productrating = "{\"productRating\":\"desc\"}";
		String pricefacet;
		if(averagecost <= 50){
			pricefacet = "{\"priceFacet\":[\"$50.00 and Under\"], \"price\" : \""+ searchlist.get(index) +"\"}";
		} else if(averagecost > 50 && averagecost <= 100){
			pricefacet = "{\"priceFacet\":[\"$100.00 and Under\"], \"price\" : \""+ searchlist.get(index) +"\"}";
		} else if(averagecost > 100 && averagecost <= 200){
			pricefacet = "{\"priceFacet\":[\"$200.00 and Under\"], \"price\" : \""+ searchlist.get(index) +"\"}";
		} else{
			pricefacet = "{\"priceFacet\":[\"$200.00 and Over\"], \"price\" : \""+ searchlist.get(index) +"\"}";
		}
		String excludeterms = "\"currentResultCount\", \"productId\",\"styleId\",\"colorId\",\"originalPrice\",\"limit\",\"originalTerm\",\"totalResultCount\",\"filters\"";
		String apikey = "a73121520492f88dc3d33daf2103d7574f1a3166";
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();

		System.out.println("sending request...");
		JsonObj obj = new JsonObj();
		try{
			//This api call retrieves the products with a given cost and sorted by product rating.
			obj = mapper.readValue(restTemplate.getForObject("http://api.zappos.com/Search?limit=100&includes=[\"productRating\"]&excludes=[{excludeterms}]&filters={pricefacet}&sort={productrating}&key={apikey}", String.class, excludeterms, pricefacet, productrating, apikey), JsonObj.class);
		}
		catch(HttpClientErrorException e){
			return null;
		}

		System.out.println("request sent...");
		System.out.println("Status Code: "+obj.getStatusCode());
		if(!obj.getStatusCode().equals("200")){
			return null;
		}
		System.out.println("size of Arraylist with duplicates: " + obj.getResults().size());

		Iterator<Results> itr = obj.getResults().iterator();
		HashSet<Results> hs = new HashSet<Results>();

		//This loop generates a list of products without any duplicates.
		while(itr.hasNext()){
			Results temp = itr.next();
			if(hs.contains(temp)){
				System.out.println("Has...");				
			}
			else {
				list.add(temp);
				hs.add(temp);
			}
		}

		System.out.println("size of Arraylist with no duplicates: " + list.size());

		return list;
	}

	//This will generate 5 combos from the list generated above.
	public ArrayList<HashMap<Results[], Double>> combolist(ArrayList<Results> list, UserInput desired, ArrayList<HashMap<Results[], Double>> combos){
		Results[][] arrobj = new Results[5][desired.getProductcount()];

		int i = 0;
		int j = 0;
		double totalamount = 0;

		//This loop also computes the total cost of the combo before placing them in a hashmap. 
		for(Results r : list){
			arrobj[i][j] = r;
			totalamount = totalamount + Double.parseDouble(r.getPrice().substring(1).replace(",", "")); //calculating total cost of the combo
			j++;

			if(j == desired.getProductcount()){
				HashMap<Results[], Double> hm = new HashMap<Results[], Double>();
				hm.put(arrobj[i], totalamount);
				combos.add(hm);
				j = 0;
				totalamount = 0;
				i++;
			}

			if(i==5)
				break;
		}

		return combos;
	}
}
