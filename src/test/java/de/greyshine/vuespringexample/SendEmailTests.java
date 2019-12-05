package de.greyshine.vuespringexample;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit4.SpringRunner;

import de.greyshine.vuespringexample.email.Attachment;
import de.greyshine.vuespringexample.email.EmailService;
import de.greyshine.vuespringexample.email.EmailService.AddressTo;
import de.greyshine.vuespringexample.utils.Utils;

@RunWith( SpringRunner.class )
@SpringBootTest( )
@EnableScheduling
public class SendEmailTests {

	//final static String EMAIL_RECEIVER = "bla@blubb.nirgends";
	final static String EMAIL_RECEIVER = "dirk.s.schumacher@web.de";
	
    @Autowired
    public JavaMailSender emailSender;
    
    @Autowired
    private EmailService emailService;
	
	@Test
	public void test() throws IOException, URISyntaxException {
		
		final URL i =  ClassLoader.getSystemClassLoader().getResource( "email-templates/image.jpg" );
		final byte[] bytes = Files.readAllBytes( Paths.get( i.toURI() ) );
		
		final Map data = new HashMap<>();
        data.put( "time" , Utils.formatDate("yyyy-MM-dd HH:mm.ss.SSS", LocalDateTime.now() ));
        data.put( "image1.jpg", bytes );
        data.put( "image2.jpg", new ByteArrayInputStream( bytes ) );
        data.put( "image3.jpg", i );
        data.put( "image4.jpg", i.toURI() );
        
        final List<Attachment> attachments = new ArrayList<>();
        attachments.add( new Attachment("attachment1.txt", "text/plain; charset=\"UTF-8\"", String.valueOf( LocalDateTime.now() ).getBytes( Utils.CHARSET_UTF8 )) );
        attachments.add( new Attachment("readme.txt", "text/plain; charset=\"UTF-8\"", ClassLoader.getSystemClassLoader().getResource( "email-templates/readme.txt" )));
        
        emailService.send( "test-template", 
        				   AddressTo.build( EMAIL_RECEIVER ),
        				   data, 
        				   attachments
       );
        
		
		final long waitFlag = emailService.getCountSendmailInvocations() + 2;
        
        while( waitFlag > emailService.getCountSendmailInvocations() ) {
        	Utils.threadWait( 10000L );	
        }
        
       System.out.println( "done" );
	}
	
}
