package com.shaic.claim.lumen.searchcoordinator;

import com.shaic.arch.table.Page;
import com.shaic.arch.table.Searchable;
import com.shaic.claim.lumen.create.LumenRequestDTO;

public interface SearchLumenCoordinatorRequestView extends Searchable{
	public void renderTable(Page<LumenRequestDTO> tableRows);
}
