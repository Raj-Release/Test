package com.shaic.reimbursement.investigation.assigninvestigation.search;

import java.util.List;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import com.shaic.domain.Intimation;
import com.shaic.newcode.wizard.domain.MappingUtil;


/**
 * @author ntv.narenj
 *
 */
public class SearchAssignInvestigationMapper {

	
	static SearchAssignInvestigationMapper myObj;

	private static MapperFacade tableMapper;
	
	public static void getAllMapValues()   {
		MapperFactory mapperFactor = MappingUtil.getMapperFactory(true);
		ClassMapBuilder<Intimation, SearchAssignInvestigationTableDTO> intimationClassMap = mapperFactor.classMap(Intimation.class, SearchAssignInvestigationTableDTO.class);
		
		intimationClassMap.field("key", "key");
		intimationClassMap.field("intimationId", "intimationNo");
		intimationClassMap.field("policy.policyNumber", "policyNo");
		intimationClassMap.field("policy.lobId", "lOBId");
		intimationClassMap.field("cpuCode.cpuCode", "cpuCode");
		intimationClassMap.field("admissionReason", "reasonForAdmission");
		intimationClassMap.field("insured.insuredName", "insuredPatientName");
		intimationClassMap.field("hospital", "hospitalNameID");
		intimationClassMap.field("policy.productName", "productName");
		
		intimationClassMap.register();
		
		tableMapper = mapperFactor.getMapperFacade();
	}
	
	public static List<SearchAssignInvestigationTableDTO> getIntimationDTO(List<Intimation> intimationData){
		List<SearchAssignInvestigationTableDTO> mapAsList = 
										tableMapper.mapAsList(intimationData, SearchAssignInvestigationTableDTO.class);
		return mapAsList;
		
	}
	public static SearchAssignInvestigationMapper getInstance(){
        if(myObj == null){
            myObj = new SearchAssignInvestigationMapper();
            getAllMapValues();
        }
        return myObj;
	 }
	
}
