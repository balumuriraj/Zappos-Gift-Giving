Zappos-Gift-Giving
==================
This application take 2 inputs from user. One is Number of products and other is desired total cost. The application then calculates the average cost of a product. It is used to define the priceFacet when querying, that narrows down the result. The application's first request is to get the list of Prices and the corresponding count of products. For example, if a user's average product cost is $45, the application first gets the list of prices and their corresponding product count using the filter ("$50.00 and under" priceFacet) sorted by price. The application then checks for average price in the above list and if it doesnt exist, it gets the immediate below value to the average price. Now another request is sent to the API using this price as a filter and sorted by product rating. Now this gives a list of products and duplicates in this list are then removed. If the count of this products is less than the required count (5 * user's desired number of products), it again calls the api for products with a price just below/above the current price as the filter, untill it gets the required number of products. Once it gets the list of products, they are then passed from the controller to the view to display 5 combinations of products.

This is a web application developed in JAVA using Spring framework. 
It uses resttemplate to communicate with the Zappos Rest Web Service and retrieves a JSON object.
The application uses jackson-mapper-asl to convert JSON object retrieved from the Zappos API into Java Objects.

IDE: Eclipse Kepler
Language: JAVA
Framework: Spring

Issues you may face to run this application:
After downloading this project from the Git, you may need to create src/main/resources folder to run this application.
