package de.greyshine.vuespringexample.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import de.greyshine.vuespringexample.db.entity.ConditionsAgreement;
import de.greyshine.vuespringexample.test.UserInfo;
import de.greyshine.vuespringexample.utils.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ContractAgreementServiceTests {
	
	@Autowired
	private ContractAgreementService contractAgreementService;

	static final String TESTTEXT; 
	
	static {
		String text = ""; 
		for(int i=0; i<255; i++) {
			text+=i;
		}
		TESTTEXT = text;
		Assert.assertTrue( TESTTEXT.length() > 255 );
	}
	
	@Autowired
	private UserService userService;
	
	@Before
	public void preTest() {
		UserInfo.TEST1.ensureDbExistance( userService );
		UserInfo.TEST2.ensureDbExistance( userService );
	}
	
	@Test
	public void save() {
		
		contractAgreementService.save( LocalDateTime.now(), "textcontext", TESTTEXT );
		
		try {
			contractAgreementService.save( null, "textcontext", TESTTEXT );
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// ok 
		}
		
		try {
			contractAgreementService.save( LocalDateTime.now(), null, TESTTEXT);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// ok 
		}
		
		try {
			contractAgreementService.save( LocalDateTime.now(), "test_context", null );
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// ok 
		}
	}
	
	@Test
	public void testNeededConfirmations() {
		
		final LocalDateTime ldt1 = LocalDate.of(2001 , 01, 01).atStartOfDay();
		final LocalDateTime ldt2 = LocalDate.of(2002 , 02, 02).atStartOfDay();
		final LocalDateTime ldt31 = LocalDate.of(2003 , 03, 03).atStartOfDay();
		final LocalDateTime ldt32 = LocalDate.of(2003 , 03, 04).atStartOfDay();
		
		final long id_d1_ctx1 = contractAgreementService.save(ldt1, "ctx1", "c1.1 test "+ Utils.formatDate(ldt1)+"\n"+ TESTTEXT );
		final long id_d1_ctx2 = contractAgreementService.save(ldt1, "ctx2", "c2.1 test "+ Utils.formatDate(ldt1)+"\n"+ TESTTEXT );
		final long id_d2_ctx1 = contractAgreementService.save(ldt2, "ctx1", "c1.2 test "+ Utils.formatDate(ldt2)+"\n"+ TESTTEXT );
		final long id_d2_ctx2 = contractAgreementService.save(ldt2, "ctx2", "c2.2 test "+ Utils.formatDate(ldt2)+"\n"+ TESTTEXT );
		final long id_d31_ctx1 = contractAgreementService.save(ldt31, "ctx1", "c1.3 test "+ Utils.formatDate(ldt31)+"\n"+ TESTTEXT );
		final long id_d32_ctx2 = contractAgreementService.save(ldt32, "ctx2", "c2.3 test "+ Utils.formatDate(ldt32)+"\n"+ TESTTEXT );
		
		List<ConditionsAgreement> cas = contractAgreementService.getLatestConfirmations();
		Assert.assertTrue( cas.size() >= 2 );
		
		cas.stream()
			.filter( ca -> ca.getContext().equals( "ctx1" ) )
			.forEach( ca-> {
				System.out.println( "LATEST-1: "+ ca);
				Assert.assertEquals( ldt31 , ca.getValidFrom() );
			} );
		
		cas.stream()
		.filter( ca -> ca.getContext().equals( "ctx2" ) )
		.forEach( ca-> {
			System.out.println( "LATEST-2: "+ ca);
			Assert.assertEquals( ldt32 , ca.getValidFrom() );
		} );

		
		List<Long> caIds = contractAgreementService.getNeededConfirmationIds( UserInfo.TEST2.login );
		caIds.forEach( caId -> System.out.println( caId ) );
		Assert.assertTrue(caIds.size() > 0 );
		
		contractAgreementService.saveUserConfirmation( UserInfo.ADMIN.login, id_d2_ctx2 );
		contractAgreementService.saveUserConfirmation( UserInfo.TEST1.login, id_d31_ctx1 );
		
		final Set<String> contexts = new HashSet<String>();
		
		cas = contractAgreementService.getNeededConfirmations( UserInfo.ADMIN.login );
		cas.stream()
		.forEach( ca -> {
			
			System.out.println( "neededConfirmation ADMIN: "+ ca );
			
			Assert.assertNotNull( ca.getContext() );
			Assert.assertFalse( contexts.contains( ca.getContext() ) );
		} );
		Assert.assertTrue( cas.isEmpty() );
		
		
		cas = contractAgreementService.getNeededConfirmations( UserInfo.TEST1.login );
		cas.stream()
		.filter( ca -> ca.getContext().startsWith( "ctx" ) )
		.forEach( ca -> {
			
			System.out.println( "neededConfirmation: "+ ca );
			
			Assert.assertNotNull( ca.getContext() );
			Assert.assertFalse( contexts.contains( ca.getContext() ) );
			
			contexts.add( ca.getContext() );
		} );
		
		Assert.assertEquals( 1, contexts.size() );
	}
	
	

}
