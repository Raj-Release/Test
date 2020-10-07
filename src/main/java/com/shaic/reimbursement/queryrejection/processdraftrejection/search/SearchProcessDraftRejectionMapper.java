package com.shaic.reimbursement.queryrejection.processdraftrejection.search;

import java.util.List;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import com.shaic.domain.Claim;
import com.shaic.newcode.wizard.domain.MappingUtil;

/**
 * @author ntv.narenj
 *
 */
public class SearchProcessDraftRejectionMapper {
	
	static SearchProcessDraftRejectionMapper  myObj;

	private static MapperFacade tableMapper;
	public static void getAllMapValues()  {
 
		MapperFactory mapperFactor = MappingUtil.getMapperFactory(true);
		ClassMapBuilder<Claim, SearchProcessDraftRejectionTableDTO> claimClassMap = mapperFactor.classMap(Claim.class, SearchProcessDraftRejectionTableDTO.class);
		claimClassMap.field("claimId", "claimNo");
		claimClassMap.field("key", "claimKey");
		claimClassMap.field("intimation.key", "intimationkey");
		claimClassMap.field("intimation.intimationId", "intimationNo");
		claimClassMap.field("intimation.policy.policyNumber", "policyNo");
		claimClassMap.field("intimation.admissionReason", "reasonForAdmission");
		claimClassMap.field("intimation.cpuCode.cpuCode", "cpuId");
		claimClassMap.field("intimation.insured.insuredName", "insuredPatientName");
		claimClassMap.field("intimation.hospital", "hospitalNameID");
		claimClassMap.field("status.processValue", "claimStatus");
		claimClassMap.register();
		
		tableMapper = mapperFactor.getMapperFacade();
	}
	
	public static List<SearchProcessDraftRejectionTableDTO> getClaimDTO(List<Claim> claimData){
		List<SearchProcessDraftRejectionTableDTO> mapAsList = 
										tableMapper.mapAsList(claimData, SearchProcessDraftRejectionTableDTO.class);
		return mapAsList;
		
	}
	
	public static SearchProcessDraftRejectionMapper getInstance(){
        if(myObj == null){
            myObj = new SearchProcessDraftRejectionMapper();
            getAllMapValues();
        }
        return myObj;
	 }
}
