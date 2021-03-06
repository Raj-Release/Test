package com.shaic.branchmanagerfeedback;

import java.io.Serializable;
import java.util.Date;

import com.shaic.claim.registration.ackhoscomm.search.AbstractSearchDTO;

public class ClaimsFeedbackDTO  extends AbstractSearchDTO implements Serializable{

	private String branchDetails;
	private Date reportedDate;
	private String feedbackType;
	private String feedbackStatus;
	private String feedbackremarks;
	private String claimsDepartmentReply;
	public String getBranchDetails() {
		return branchDetails;
	}
	public void setBranchDetails(String branchDetails) {
		this.branchDetails = branchDetails;
	}
	public Date getReportedDate() {
		return reportedDate;
	}
	public void setReportedDate(Date reportedDate) {
		this.reportedDate = reportedDate;
	}
	public String getFeedbackType() {
		return feedbackType;
	}
	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}
	public String getFeedbackStatus() {
		return feedbackStatus;
	}
	public void setFeedbackStatus(String feedbackStatus) {
		this.feedbackStatus = feedbackStatus;
	}
	public String getFeedbackremarks() {
		return feedbackremarks;
	}
	public void setFeedbackremarks(String feedbackremarks) {
		this.feedbackremarks = feedbackremarks;
	}
	public String getClaimsDepartmentReply() {
		return claimsDepartmentReply;
	}
	public void setClaimsDepartmentReply(String claimsDepartmentReply) {
		this.claimsDepartmentReply = claimsDepartmentReply;
	}
	
	
}
