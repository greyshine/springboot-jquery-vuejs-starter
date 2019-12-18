package de.greyshine.vuespringexample.db.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.greyshine.vuespringexample.db.entity.ConditionsAgreement;

public interface ContractAgreementRepository extends CrudRepository<ConditionsAgreement, Long>  {

	@Query( name="ContractAgreement.findLatest" )
	List<ConditionsAgreement> findLatest();

	@Query( name="ContractAgreement.findUndeclaredConfirmations" )
	List<ConditionsAgreement> findUndeclaredConfirmations(String userLogin);
	
	@Query( name="ContractAgreement.findUndeclaredConfirmationIds" )
	List<Long> findUndeclaredConfirmationIds(String userLogin);
}
