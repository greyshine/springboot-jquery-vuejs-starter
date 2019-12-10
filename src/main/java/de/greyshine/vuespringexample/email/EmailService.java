package de.greyshine.vuespringexample.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.greyshine.vuespringexample.email.entitys.EmailBinary;
import de.greyshine.vuespringexample.email.entitys.EmailEntity;
import de.greyshine.vuespringexample.email.entitys.EmailBinary.Type;
import de.greyshine.vuespringexample.utils.Utils;

@Service
public class EmailService {
	
	private final static Logger LOG = LoggerFactory.getLogger( EmailService.class );
	
	final static MimetypesFileTypeMap MIMETYPES = new MimetypesFileTypeMap();
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
    public JavaMailSender emailSender;
	
	@Value("${mail.sender.email}")
	private String fromEmail;
	
	private Map<String,EmailTexts> templates = new HashMap<>();
	
	@Value( value="${mail.forceTemplateReload:false}")
	private boolean forceTemplateReload = true;
	
	@Value( value="${mail.maxSendFailures:5}")
	private int maxSendFailures;
	
	@Value( value="${mail.resendWaitSeconds:120}")
	private int resendWaitSeconds;
	
	private final AtomicLong mailSendInvocations = new AtomicLong(0L);
	
	/**
	 * 
	 * @param templateName name of the template in folder /email-templates without file ending 'et'
	 *   
	 */
	@Transactional
	public void send(String templateName, AddressTo addresTo, Map<String,?> dataMap, List<Attachment> attachments) {
		
		Assert.isTrue( Utils.isNotBlank(templateName), "template reference must not be blank" );
		Assert.notNull( addresTo, "AddressTo must not be null" );
		
		dataMap =  dataMap != null ? new HashMap<>(dataMap) : Collections.emptyMap(); 
		attachments = attachments != null ? attachments : new ArrayList<>();
		
		final EmailTexts templateData = getTemplate( templateName );
		if ( templateData == null ) { throw new IllegalStateException("template is blank [template="+ templateName +"]"); }
		
		final EmailEntity email = new EmailEntity();
		
		// setup 
		final Set<String> keysToRemove = new HashSet<>();
		for( Entry<String,?> aEntry : dataMap.entrySet() ) {
			
			if ( Utils.isBlank( aEntry.getKey() ) ) { throw new IllegalStateException("dataMap constains empty key"); }
			
			final Object value = aEntry.getValue();
			if ( value == null ) { continue; }
			
			byte[] data = null;
			
			if ( value instanceof byte[] ) {
			
				data = (byte[])value;
			
			} else if ( value instanceof InputStream ) {
			
				try {
					
					data = Utils.executeAndClose( (InputStream)value, is -> is.readAllBytes() );	
					
				} catch (Exception e) {
					throw Utils.toRuntimeException(e);
				}
				
			
			} else if ( value instanceof URL ) {
				
				try {
					
					data = Utils.executeAndClose( ((URL)value).openStream(), is -> is.readAllBytes() );	
					
				} catch (Exception e) {
					throw Utils.toRuntimeException(e);
				}
				
			} else if ( value instanceof URI ) {
				
				try {
					
					data = Utils.executeAndClose( ((URI)value).toURL().openStream(), is -> is.readAllBytes() );	
					
				} catch (Exception e) {
					throw Utils.toRuntimeException(e);
				}
			}
			
			if ( data != null ) {
				
				keysToRemove.add( aEntry.getKey() );
				
				String mimetype = MIMETYPES.getContentType( aEntry.getKey() );
				mimetype = mimetype != null ? mimetype : "*/*";
				
				final EmailBinary ae = new EmailBinary();
				ae.type = EmailBinary.Type.INLINE;
				ae.name = aEntry.getKey();
				ae.contentType = mimetype;
				ae.data = data;
				
				email.addData( ae );
			}
		}
		
		// remove keys w/ INLINE data
		for(String k : keysToRemove) { dataMap.remove( k ); }
		
		final String subject = replaceVars( templateData.subject, dataMap );
		final String html = replaceVars( templateData.html, dataMap );
		final String plaintext = replaceVars( templateData.plaintext, dataMap );
		
		email.setTos( emailTos(addresTo) );
		email.setSubject( subject );
		email.setHtml( html );
		email.setPlaintext( plaintext );
		
		for( Attachment attachment : attachments ) {
			
			if ( attachment == null ) { continue; }
			final EmailBinary ae = new EmailBinary();
			ae.type = EmailBinary.Type.ATTACHMENT;
			ae.name = Utils.trimOrDefaultIfBlank( attachment.name, ()->UUID.randomUUID().toString() );
			ae.contentType = Utils.trimToNull( attachment.contentType );
			try {
				ae.data = attachment.inputStream.readAllBytes();	
			} catch (Exception e) {
				throw Utils.toRuntimeException(e);
			} finally {
				Utils.close( attachment );
			}
			
			email.addData( ae );
		}
		
		em.persist( email );
	}
	
	private static String replaceVars(String text, Map<String, ?> dataMap) {
		
		if ( text == null || dataMap == null ) { return text; }
		
		for(Entry<String,?> e : dataMap.entrySet()) {
			
			if ( Utils.isBlank( e.getKey() ) ) { continue; }
			
			text = text.replace("{{"+ e.getKey() +"}}", String.valueOf( e.getValue()==null?"":e.getValue() ));
		}
		
		return text;
	}

	public long getCountSendmailInvocations() {
		return mailSendInvocations.get();
	}
	
	@Scheduled( fixedDelayString = "${mail.sendInterval:10000}" )
	@Transactional
	public void sendEmails() {

		mailSendInvocations.addAndGet(1L);
		
		LOG.info( "Sending emails ..." );
		
		@SuppressWarnings("unchecked")
		final List<EmailEntity> emails = (List<EmailEntity>)em.createQuery(
				"FROM EmailEntity e\n"+
				"WHERE 1=1\n"+
				"AND e.send = null\n"+
				"AND e.failed = null\n"
		).getResultList();
		
		if ( emails.isEmpty() ) {
			LOG.debug( "no new emails..." );
			return;
		}
		
		// !do not modify the Email entity since update property is used as business logic!
		emails.forEach( email -> sendEmail( email ) );
	}
	
	private void sendEmail(EmailEntity email) {
		
		if ( email == null ) { return; }
		
		// !do not modify the Email entity since update property is used as business logic!
		if ( email.getSendFailures() > 0 ) {
			
			final LocalDateTime timeToResend = email.getUpdated().plusSeconds( resendWaitSeconds );
			
			if ( LocalDateTime.now().isAfter( timeToResend ) ) {
				// not yet subject to be resend; wait longer
				return;
			}
		}

		
		if ( Utils.isAllBlank( email.getSubject(), email.getHtml(), email.getPlaintext() ) ) {
			email.setFailed( LocalDateTime.now() );
			return;
		}
		
		
		MimeMessagePreparator mmp = null;
		try {

			mmp = buildMimeMessagePreparator(email);
			
		} catch(Exception e) {
			LOG.error( "failed to setup email: {}; {}", email, Utils.toString(e), e );
			email.setFailed( LocalDateTime.now() );
			return;
		}
		

		if ( email.getFailed() == null ) {
			
			try {
				
				emailSender.send( mmp );
				email.setSend( LocalDateTime.now() );
				
			} catch (Exception e) {
				
				LOG.error( "error sending email: {}; {}", email, Utils.toString(e), e );
				
				final int sendFailures = email.addSendFailure();
				
				if ( maxSendFailures >= sendFailures ) {
					LOG.error( "marked email as not send: {}", email );
					email.setFailed( LocalDateTime.now() );
				}
			}
		}
	}

	private MimeMessageHelper buildMimeMessageHelper(MimeMessage mimeMessage, EmailEntity emailEntity) throws MessagingException {
		
		final String html = Utils.trimToNull( emailEntity.getHtml() );
        // no text/content at all seems not allowed so we put an empty string
		final String plaintext = html == null && Utils.isBlank( emailEntity.getPlaintext() ) ? "" : Utils.trimToNull( emailEntity.getPlaintext() );
		final List<EmailBinary> attachmentBinarys = emailEntity.getDatas( Type.ATTACHMENT );
		
		final boolean isMultipart = (html != null && plaintext != null) || !attachmentBinarys.isEmpty();
		
		final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, isMultipart );
		
		messageHelper.setFrom( fromEmail );
        messageHelper.setTo( emailEntity.getTos() );
        messageHelper.setCc( "kuemmel.dss@gmx.de" );
		messageHelper.setSubject( Utils.trimToEmpty( emailEntity.getSubject() ) );
        
		// !note the order of setting text first plaintext and then html is important
        // the first wins over the second one (feels like an implementation issue on Java side)
        if ( plaintext != null ) {
        	messageHelper.setText( plaintext, false );
        }
		
        if ( html != null ) {
        	messageHelper.setText( html, true );
        }
        
        for( EmailBinary eb : emailEntity.getDatas( Type.INLINE )  ) {
        	messageHelper.addInline(eb.name, ()->new ByteArrayInputStream( eb.data ), eb.contentType);
        }
        
        try {
			
        	for( EmailBinary eb : attachmentBinarys ) {
        		LOG.debug( "emailBinary: {}", eb );
        		messageHelper.addAttachment(eb.name, ()->new ByteArrayInputStream(eb.data), eb.contentType);	
        	}
        	
		} catch (Exception e) {
			LOG.error( "{}", Utils.toString( e ), e );
			emailEntity.setFailed( LocalDateTime.now() );
			throw e instanceof MessagingException ? (MessagingException)e : new MessagingException( "Attachment handling failed: "+ e.getMessage() , e);
		}
        
        return messageHelper;
	}
	
	private MimeMessagePreparator buildMimeMessagePreparator(EmailEntity emailEntity) {
		return mimeMessage -> buildMimeMessageHelper(mimeMessage, emailEntity);
	}

	private EmailTexts getTemplate(String templateName) {
		
		if ( !forceTemplateReload && templates.containsKey( templateName ) ) {
			return templates.get( templateName );
		}
		
		try {
			
			final String resource = "email-templates/"+ templateName +".et";
		
			String templateString = Utils.getResource( resource, Utils.CHARSET_UTF8 );
			
			if ( templateString == null ) { return null; }
			
			templateString = templateString.replace("\r", "");
			
			String subject = "", html = "", plaintext = "";
			
			int idx = templateString.indexOf( '\n' );
			subject = idx < 1 ? templateString.strip() : templateString.substring( 0, idx );
			String text = idx < 1 ? "" : templateString.substring( idx );
			
			// TODO extract text part
			boolean plaintextRead = false;
			for (String aLine : text.split("\n", -1) ) {
			
				if ( "-".equals(aLine.stripTrailing()) ) {
					
					plaintextRead = true;
					continue;
					
				} else if ( !plaintextRead ) {
					
					if ( "--".equals( aLine.stripTrailing() ) ) {
						aLine = "-";
					}
					
					html += aLine +"\n";
					
				} else {
				
					plaintext += aLine +"\n";
				}
			}
			
			if ( Utils.isNotBlank( html ) ) {
				html =  "<html>\n"+
						"  <head>\n"+
					    "    <meta http-equiv=\"Content-Type\" content=\"text/html charset=UTF-8\" />\n" +
						"  <body>\n" +
					    "          "+ html.trim().replaceAll("\n","<br/>") + "\n" +
					    "  </body>\n"+
					    "</html>";
			}
			
			subject = Utils.trimToNull( subject );
			html = Utils.trimToNull( html );
			plaintext = Utils.trimToNull( plaintext );
			
			templates.put( templateName, new EmailTexts(subject, html, plaintext) );
			
			LOG.debug( "read {}\nsubject: {}\n\nhtml:\n\n{}\n\nplaintext:\n\n{}", resource, subject, html, plaintext );
			
			
		} catch (IOException e) {
			throw Utils.toRuntimeException(e);
		}
		
		return templates.get( templateName );
	}

	private static String emailTos(AddressTo... aTos) {
		
		if ( aTos == null ) { return null; }
		
		final StringBuffer sb = new StringBuffer();
		
		for(int i=0, l=aTos.length; i<l; i++) {
			
			final AddressTo a = aTos[i];
			
			if ( Utils.isNotBlank( a.name ) ) { sb.append( a.name ).append( ' ' ); }
			sb.append( '<' ).append( a.email ).append( '>' );
			
			if ( i < l-1 ) { sb.append( ", " ); }
		}
		
		return sb.toString();
	}
	
	public static class AddressTo {
	
		public final String name;
		public final String email;
		
		public AddressTo(String name, String email) {
			
			this.name = Utils.trimToNull( name );
			email = Utils.trimToNull( email );
			
			if ( email == null ) { throw new IllegalArgumentException("email must not be blank"); }
			
			this.email = email;
		}
		
		public static AddressTo build(String email) {
			return build( null, email );
		}
		
		public static AddressTo build(String name, String email) {
			return new AddressTo( name, email );
		}
	}
		
	private class EmailTexts {
		
		final String subject;
		final String html;
		final String plaintext;
		
		EmailTexts(String subject, String html, String plaintext) {
			this.subject = Utils.strip(subject);
			this.html = Utils.strip(html);
			this.plaintext = Utils.strip(plaintext);
		}
	}
}
