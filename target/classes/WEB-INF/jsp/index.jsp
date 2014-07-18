<%@page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Zappos Gift Giving</title>
</head>
<body
	style="font-family: Verdana, Arial, sans; font-size: 12px; background: #F8F8F8; min-width: 1300px;">
	<h1 style="text-align: center">Zappos Gift Giving Application</h1>
	<div
		style="width: 70%; margin: 0 auto; background: white; padding: 20px;">

		Please enter the number of products you want to buy and your desired
		total cost. We will give you the combos sorted to product rating:<br>
		<br>
		<form:form method="post" modelAttribute="desired">
			<table>
				<tr>
					<td><b>Number of Products:</b></td>
					<td><form:input path="productcount"></form:input></td>
					<c:if test="${not empty error1}">
						<td>${error1}</td>
					</c:if>
				</tr>
				<tr>
					<td><b>Desired Total Amount: $</b></td>
					<td><form:input path="amount"></form:input></td>
					<c:if test="${not empty error2}">
						<td>${error2}</td>
					</c:if>
				</tr>
				<tr>
					<td></td>
					<td><input type="submit" value="submit" /></td>
				</tr>

			</table>
		</form:form>
	</div>

	<br>
	<br>

	<c:if test="${not empty error}">
		<div
			style="width: 80%; margin: 0 auto; background: white; padding: 20px; text-align: center;">
			${error}</div>
	</c:if>

	<c:if test="${not empty combos}">
		<div
			style="width: 80%; margin: 0 auto; background: white; padding: 20px;">
			<h2>Results:</h2>
			<hr>
			<c:forEach var="item" items="${combos}" varStatus="count">
				<c:forEach var="hm" items="${item}">
					<div style="text-align: center;">
						<h3>
							Combo ${count.count}: Total Cost - $${hm.value}
							<c:if test="${hm.value > desired.amount }">
								<span style="color: red; font-size: 10.5px;"> - Exceeded
									desired total cost</span>
							</c:if>
						</h3>
					</div>
					<hr>
					<br>
					<c:forEach var="hmobj" items="${hm.key}">
						<div
							style="display: inline-block; margin: 5px; width: 28%; background: #F8F8F8; padding: 20px;">
							<div style="text-align: center">
								<b>${hmobj.productName}</b>
								<c:if test="${hmobj.productRating == 0}">
									<span style="background: yellow; padding: 2px;"> NEW</span>
								</c:if>
							</div>
							<br>
							<table style="border-collapse: separate; border-spacing: 10px;">
								<tr>
									<td><img src="${hmobj.thumbnailImageUrl}" /></td>
									<td>Brand : ${hmobj.brandName} <br> <br> Price:
										${hmobj.price} <br> <br> % off: ${hmobj.percentOff}<br>
										<br> Rating: ${hmobj.productRating}
									</td>
								</tr>
							</table>
							<br>
							<hr>
							<br>
							<div style="text-align: center">
								<a href="${hmobj.productUrl}" target="_blank"><button>Buy
										this item</button></a>
							</div>
						</div>
					</c:forEach>
				</c:forEach>
				<br>
				<br>
				<hr>
			</c:forEach>
		</div>
	</c:if>

</body>
</html>