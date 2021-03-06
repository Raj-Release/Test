package com.shaic.claim.translationmiscrequest.view;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.shaic.arch.fields.dto.AbstractDAO;
import com.shaic.domain.Intimation;
import com.shaic.domain.preauth.Coordinator;

@Stateless
public class ViewTranslationMiscRequestService extends AbstractDAO<Coordinator> {

	public ViewTranslationMiscRequestService() {
		super();
	}

	public List<ViewTranslationMiscRequestTableDTO> search(Long intmationKey) {

		final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		final CriteriaQuery<Coordinator> criteriaQuery = builder
				.createQuery(Coordinator.class);

		Root<Coordinator> searchRoot = criteriaQuery
				.from(Coordinator.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		List<Coordinator> resultList = new ArrayList<Coordinator>();
		if (intmationKey != null) {
			Predicate intimationPredicate = builder.equal(searchRoot
					.<Intimation> get("intimation")
					.<Long> get("key"),intmationKey);
			predicates.add(intimationPredicate);
		}
		criteriaQuery.select(searchRoot).where(
				builder.and(predicates.toArray(new Predicate[] {})));

		final TypedQuery<Coordinator> coordinatorQuery = entityManager
				.createQuery(criteriaQuery);

		resultList = coordinatorQuery.getResultList();

		List<ViewTranslationMiscRequestTableDTO> vieweCoOrdinatorReplyTableDTO = ViewTranslationMiscRequestMapper.getInstance()
				.getViewCoOrdinatorDTO(resultList);
		return vieweCoOrdinatorReplyTableDTO;
	}

	@Override
	public Class<Coordinator> getDTOClass() {
		// TODO Auto-generated method stub
		return Coordinator.class;
	}
	
}