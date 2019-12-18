package de.greyshine.vuespringexample.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import de.greyshine.vuespringexample.utils.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class QueryTests {
	
	@PersistenceContext
	private EntityManager em;
	
	// TODO: named query is not found, find a way to run it...
	// some stackoverflow mentioned to have a persistence.xml, which I consider to be overkill in/for a springboot running test environment
	//@Test
	@Transactional
	public void testNamedQuery() {
		
		final String queryname = "ContractAgreement.findLatest";

		final List<?> results = em.createNamedQuery( queryname ).getResultList();
		System.out.println( "named '"+ queryname +"' results: "+ results.size() );
		results.forEach( r -> System.out.println( Utils.toString(r) ) );
	}
	
	@Test
	@Transactional
	public void testJpaQuery() {
		
		final String query = "FROM User";
		
		final List<?> results = em.createQuery( query ).getResultList();
		System.out.println( "JQL results: "+ results.size() );
		results.forEach( r -> System.out.println( Utils.toString(r) ) );
	}
	
	@Test
	@Transactional
	public void testNativeQuery() {
		
		final String query = "SELECT * FROM \"user\"";
		
		final List<?> results = em.createNativeQuery( query ).getResultList();
		System.out.println( "Native results: "+ results.size() );
		results.forEach( r -> System.out.println( Utils.toString( r ) ) );
	}
	
}
