<%@page import="org.apache.http.HttpRequest"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,java.lang.*" %>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Claim Rejection Status</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <!-- <link rel="stylesheet"  href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" />
    <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css"> 
    
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css">
    <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap-theme.min.css"> -->
	
	<link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/font-awesome.css" >
    <link rel="stylesheet" href="css/bootstrap-theme.min.css">

    <script type="text/javascript" src="js/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <style>
.height {
    min-height: 200px;
}

.icon {
    font-size: 47px;
    color: #5CB85C;
}

.iconbig {
    font-size: 77px;
    color: #5CB85C;
}

.col-xs-12 col-md-12 col-lg-12 pull-left {
	align-content: spacebetween;
}
.col-xs-12 col-md-12 col-lg-12 {
	padding-left: 30px;
}
.panel panel-default height{
	padding-right: 30px;

}

.table.table.table-condensed {
	border-collapse: collapse;
}

.table > tbody > tr > .emptyrow {
    border-top: none;
}

.table > thead > tr > .emptyrow {
    border-bottom: none;
}

.table > tbody > tr > .highrow {
    border-top: 3px solid;
}

.col-lg-1, .col-lg-10, .col-lg-11, .col-lg-12, .col-lg-2, .col-lg-3, .col-lg-4, .col-lg-5, .col-lg-6, .col-lg-7, .col-lg-8, .col-lg-9, .col-md-1, .col-md-10, .col-md-11, .col-md-12, .col-md-2, .col-md-3, .col-md-4, .col-md-5, .col-md-6, .col-md-7, .col-md-8, .col-md-9, .col-sm-1, .col-sm-10, .col-sm-11, .col-sm-12, .col-sm-2, .col-sm-3, .col-sm-4, .col-sm-5, .col-sm-6, .col-sm-7, .col-sm-8, .col-sm-9, .col-xs-1, .col-xs-10, .col-xs-11, .col-xs-12, .col-xs-2, .col-xs-3, .col-xs-4, .col-xs-5, .col-xs-6, .col-xs-7, .col-xs-8, .col-xs-9 {
	position: static !important;
    min-height: 1px;
    padding-right: 15px;
    padding-left: 15px;
}

</style>

		
</head>
<body>

<%!
	String filePath;
	String queryFilepath;
 %>
<%
	session.setAttribute("intimationNumber", request.getAttribute("intimationNumber"));
	session.setAttribute("daignosisName", request.getAttribute("daignosisName"));
	session.setAttribute("preauth", request.getAttribute("preauth"));
	session.setAttribute("claim", request.getAttribute("claim"));
	session.setAttribute("reimbursementList", request.getAttribute("reimbursementList"));
	session.setAttribute("intimation", request.getAttribute("intimation"));
	session.setAttribute("hospitals", request.getAttribute("hospitals"));
	session.setAttribute("policyList", request.getAttribute("policyList"));
	session.setAttribute("rejectList", request.getAttribute("rejectList"));
	session.setAttribute("dmsUrl", request.getAttribute("dmsUrl"));
 %>

<div class="container">

<div class="page-header"> 
    <!-- <h4 class="panel-heading text-center">Status of Intimation No : ${intimation.getIntimationId()}</h4>
       <a href= "${dmsUrl}" target= "_blank" style="padding-right:15px">View Documents</a> --->
       <h4 class="panel-heading text-center">View Rejection Details</h4>
</div>
	<div class="row">
                <div class="col-xs-12 col-md-12 col-lg-12 pull-left" >
                    <div class="panel panel-default height">
                        <div class="panel-heading"><b>Intimation Details</b></div>
                        <div class="panel-body panel-default table-responsive">

						<table class="table table-condensed">
								<tbody>
		
									<tr>
										<td><strong>Intimation No</strong></td>
										<td style="width:50%">${intimation.getIntimationId()}</td>
									</tr>
									<tr>
										<td ><strong>Claim Number</strong></td>
										<td>${claim.getClaimId()}</td>
									</tr>
									<tr>
										<td ><strong>Policy No</strong></td>
										<td>${intimation.getPolicy().getPolicyNumber()} </td>
									</tr>
									<tr>
										<td ><strong>Product Name</strong></td>
										<td>${intimation.getPolicy().getProductName()}</td>
									</tr>
									<tr>
										<td ><strong>Claim Type</strong></td>
										<td>${claim.getClaimType().getValue()}</td>
									</tr>
									<tr>
										<td ><strong>Insured Patient Name</strong></td>
										<td>${intimation.getInsured().getInsuredName()} </td>
									<tr>
										<td ><strong>Hospital Name</strong></td>
										<td>${hospitals.getName()} </td>
									</tr>
									<tr>
										<td ><strong>Hospital City</strong></td>
										<td>${hospitals.getCity()} </td>
									</tr>
									<tr>
										<td ><strong>Hospital Type</strong></td>
										<td>${intimation.getHospitalType().getValue()}</td>
									</tr>
									<tr>
										<td ><strong>Date Of Admission</strong></td>
										 <td>${rejectList.getAdmissionDate()}</td>
									</tr>
									<tr>
										<td><strong>Diagnosis</strong></td>
										<td>${daignosisName} </td>
									</tr>
								</tbody>
						</table>
						</div>
					</div>
				</div>
				
				
				<!--Rejection Details List  -->
					<div class="col-xs-12 col-md-12 col-lg-12">
                    <div class="panel panel-default height">
                        <div class="panel-heading"><b>Rejection Details</b></div>
                        <div class="panel-body panel-default table-responsive">
							<table  class="table table-condensed">
								<tbody>
										<tr>
											<td><strong>Rejected By Role</strong></td>
											<td></td>
										</tr>
										<tr>
											<td ><strong>Rejected By Id/Name</strong></td>
											<td>${rejectList.getRejectedName()}</td>
										</tr>
										<tr>
											<td><strong>Rejected Date</strong></td>
											<td>${rejectList.getRejectedDate()}</td>
										</tr>
										<tr>
											<td ><strong>Rejection Letter Drafted Date</strong></td>
											<td>${rejectList.getRejectedDate()}</td>
										</tr>
										<tr>
											<td ><strong>Rejection Letter Remarks</strong></td>
											<td>${rejectList.getLetterRectionRemarks()}</td>
										</tr>
										<tr>
											<td ><strong>Rejection Approved/Disaaproved/Redraft Date</strong></td>
											<td>${rejectList.getApproveRejectionDate()}</td>
										<tr>
										<td ><strong>Rejection Remarks</strong></td>
										<td>${rejectList.getRejectionRemarks()}</td>
										</tr>
										<tr>
										<td ><strong>Rejection Dispaarove Remarks</strong></td>
										<td></td>
										</tr>
										<tr>
										<td ><strong>Rejection Redraft Remarks</strong></td>
										<td></td>
										</tr>
										<tr>
										<td ><strong>Rejection Status</strong></td>
										<td>${rejectList.getRejectionStatus()}</td>
										</tr>
									</tbody>
							</table>
						</div>
					</div>
				</div>
		</div>
	</div>
	
	<%-- <input type="text" style="border: none;visibility: hidden;" id="getIntimationCreateDate" value="${intimation.getCreatedDate()}">
	<input type="text" style="border: none;visibility: hidden;" id="getAdmissionDate" value="${intimation.getAdmissionDate()}">	
	<input type="text" style="border: none;visibility: hidden;" id="getRejectedDate" value="${rejectList.getAdmissionDate()}"> --%>
</body>

<script type="text/javascript">
		$(document).ready(function(){
				
				/* var ict = String(document.getElementById('getIntimationCreateDate').value);
				document.getElementById('setIntimationCreateDate').innerHTML = ict.charAt(8)+""+ict.charAt(9)+"/"+ict.charAt(5)+""+ict.charAt(6)+"/"+ict.charAt(0)+""+ict.charAt(1)+""+ict.charAt(2)+""+ict.charAt(3)+" "+ict.charAt(11)+""+ict.charAt(12)+":"+ict.charAt(14)+""+ict.charAt(15)+":"+ict.charAt(17)+""+ict.charAt(18);
    			
    			var adt = String(document.getElementById('getAdmissionDate').value);
    				document.getElementById('setAdmissionDate').innerHTML = adt.charAt(8)+""+adt.charAt(9)+"/"+adt.charAt(5)+""+adt.charAt(6)+"/"+adt.charAt(0)+""+adt.charAt(1)+""+adt.charAt(2)+""+adt.charAt(3)+" "+adt.charAt(11)+""+adt.charAt(12)+":"+adt.charAt(14)+""+adt.charAt(15)+":"+adt.charAt(17)+""+adt.charAt(18);
    			var rdt = String(document.getElementById('getRejectedDate').value);
    				document.getElementById('setRejectedDate').innerHTML = rdt.charAt(8)+""+rdt.charAt(9)+"/"+rdt.charAt(5)+""+rdt.charAt(6)+"/"+rdt.charAt(0)+""+rdt.charAt(1)+""+rdt.charAt(2)+""+rdt.charAt(3)+" "+rdt.charAt(11)+""+rdt.charAt(12)+":"+rdt.charAt(14)+""+rdt.charAt(15)+":"+rdt.charAt(17)+""+rdt.charAt(18);
		 */
		 });
		
		
	</script>
</html>
