package com.shaic.claim.policy.search.ui;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremPEDDetails {
	
	@JsonProperty("PEDCode")
	private String pedCode;
	
	@JsonProperty("PEDDescription")
	private String pedDescription;
	
	@JsonProperty("Code")
	private String gmcPedCode;
	
	@JsonProperty("Description")
	private String gmcPedDescription;

	public String getPedCode() {
		return pedCode;
	}

	public void setPedCode(String pedCode) {
		this.pedCode = pedCode;
	}

	public String getPedDescription() {
		return pedDescription;
	}

	public void setPedDescription(String pedDescription) {
		this.pedDescription = pedDescription;
	}

	public String getGmcPedCode() {
		return gmcPedCode;
	}

	public void setGmcPedCode(String gmcPedCode) {
		this.gmcPedCode = gmcPedCode;
	}

	public String getGmcPedDescription() {
		return gmcPedDescription;
	}

	public void setGmcPedDescription(String gmcPedDescription) {
		this.gmcPedDescription = gmcPedDescription;
	}

	

	
}
