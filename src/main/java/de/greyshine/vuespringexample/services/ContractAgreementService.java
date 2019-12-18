package de.greyshine.vuespringexample.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.greyshine.vuespringexample.db.entity.ConditionsAgreement;
import de.greyshine.vuespringexample.db.entity.UserConditionsAgreement;
import de.greyshine.vuespringexample.db.repos.ContractAgreementRepository;
import de.greyshine.vuespringexample.utils.Utils;

@Service
public class ContractAgreementService {

	@SuppressWarnings("unused")
	private final static Logger LOG = LoggerFactory.getLogger(ContractAgreementService.class);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ContractAgreementRepository contractAgreementRepository;

	@Transactional
	public long save(LocalDateTime validFrom, String context, String text) {

		Assert.notNull(validFrom, "no date given");
		Assert.isTrue(Utils.isNotBlank(context), "context is blank");
		Assert.isTrue(Utils.isNotBlank(text), "text is blank");

		ConditionsAgreement contractAgreement = new ConditionsAgreement();
		contractAgreement.setValidFrom(validFrom);
		contractAgreement.setContext(context.strip());
		contractAgreement.setText(text.strip());

		contractAgreement = contractAgreementRepository.save(contractAgreement);
		
		return contractAgreement.getId();
	}

	@Transactional
	public List<ConditionsAgreement> getNeededConfirmations(String userLogin) {
		return Utils.isBlank( userLogin ) ? Collections.emptyList() : contractAgreementRepository.findUndeclaredConfirmations( userLogin );
	}
	
	@Transactional
	public List<Long> getNeededConfirmationIds(String userLogin) {
		return Utils.isBlank( userLogin ) ? Collections.emptyList() : contractAgreementRepository.findUndeclaredConfirmationIds( userLogin );
	}

	@Transactional
	public int setConfirmations(String login, List<Long> confirmationIds) {
		
		if ( Utils.isBlank( login ) ) { return 0; }
		if ( confirmationIds == null || confirmationIds.isEmpty() ) { return 0; }
		
		int count=0;
		for( Long cId : confirmationIds ) {
			
			if ( cId == null || !contractAgreementRepository.existsById( cId ) ) { continue; }
			
			UserConditionsAgreement uca = em.find( UserConditionsAgreement.class , new UserConditionsAgreement.Pk( login, cId ) );
			// TODO: call count/exists instead of object fetching
			if ( uca != null ) { continue; }
			
			uca = new UserConditionsAgreement();
			uca.setLogin( login );
			uca.setConditionsAgreementId( cId );
			
			em.persist( uca );

			count++;
			LOG.info("created: {}", uca);
		}
		
		return count;
	}
	
	@Transactional
	public int setConfirmations(String login) {
		
		final List<Long> confirmationIds = getNeededConfirmationIds( login );
		if ( confirmationIds.isEmpty() ) { return 0; }
		
		return setConfirmations(login, confirmationIds);
	}

	@Transactional
	public List<ConditionsAgreement> getLatestConfirmations() {
		return contractAgreementRepository.findLatest();
	}

	@Transactional
	public void saveUserConfirmation(String login, long contractAgreementId) {
		
		Assert.isTrue( Utils.isNotBlank(login) , "login is blank" );
		
		// TODO: check if entry exists
		
		final UserConditionsAgreement uca = new UserConditionsAgreement();
		uca.setLogin( login );
		uca.setConditionsAgreementId( contractAgreementId );
		
		em.persist( uca );
	}

	

}
