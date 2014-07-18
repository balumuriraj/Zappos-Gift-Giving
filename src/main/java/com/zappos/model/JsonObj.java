package com.zappos.model;

import java.util.ArrayList;

//This class is mapped to the Json which has the products with details of a particular price. 
public class JsonObj {

	private String statusCode;
	private ArrayList<Results> results;
	private String error;
	
	//Getters and Setters
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public ArrayList<Results> getResults() {
		return results;
	}
	public void setResults(ArrayList<Results> results) {
		this.results = results;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}


	//This class consists of a product details
	public static class Results{
		
		private String price;
		private String productName;
		private String brandName;
		private String percentOff;
		private String productUrl;
		private String productRating;
		private String thumbnailImageUrl;
		
		//Getters and Setters
		public String getBrandName() {
			return brandName;
		}
		public void setBrandName(String brandName) {
			this.brandName = brandName;
		}
		public String getPercentOff() {
			return percentOff;
		}
		public void setPercentOff(String percentOff) {
			this.percentOff = percentOff;
		}
		public String getProductUrl() {
			return productUrl;
		}
		public void setProductUrl(String productUrl) {
			this.productUrl = productUrl;
		}
		public String getProductRating() {
			return productRating;
		}
		public void setProductRating(String productRating) {
			this.productRating = productRating;
		}
		public String getThumbnailImageUrl() {
			return thumbnailImageUrl;
		}
		public void setThumbnailImageUrl(String thumbnailImageUrl) {
			this.thumbnailImageUrl = thumbnailImageUrl;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((price == null) ? 0 : price.hashCode());
			result = prime * result
					+ ((productName == null) ? 0 : productName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Results other = (Results) obj;
			if (price == null) {
				if (other.price != null)
					return false;
			} else if (!price.equals(other.price))
				return false;
			if (productName == null) {
				if (other.productName != null)
					return false;
			} else if (!productName.equals(other.productName))
				return false;
			return true;
		}
		
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		
	}
}
