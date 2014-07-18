package com.zappos.model;

import java.util.ArrayList;

//Class that is mapped to the Json which consists of procduct counts and costs that is retrieved from API 
public class JsonFacetObj {
	
	//attributes retrieved from Json will be mapped to the below variables of this class
	private String statusCode;
	private ArrayList<Facets> facets;
	private String error;
	
	//Getters and Setters
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public ArrayList<Facets> getFacets() {
		return facets;
	}

	public void setFacets(ArrayList<Facets> facets) {
		this.facets = facets;
	}

	//This class consists of cost and its corresponding count of products
	public static class Values{
		String name;
		int count;
		
		//Getters and Setters
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		
		
	}
	
	//This class consists of any array of nodes that has costs and its corresponding count of products
	public static class Facets{
		private ArrayList<Values> values;

		//Getters and Setters
		public ArrayList<Values> getValues() {
			return values;
		}

		public void setValues(ArrayList<Values> values) {
			this.values = values;
		}
		
		
	}
}
