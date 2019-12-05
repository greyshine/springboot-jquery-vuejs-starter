package de.greyshine.vuespringexample.email;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.greyshine.vuespringexample.utils.Utils;

import static de.greyshine.vuespringexample.email.EmailService.MIMETYPES;

public class Attachment implements Closeable {
	
	final String name;
	final String contentType;
	final InputStream inputStream;

	public Attachment(String name, String contentType, InputStream inputStream) {
		if ( Utils.isBlank(contentType) ) { throw new IllegalArgumentException("contentType is blank"); }
		if ( inputStream == null ) { throw new IllegalArgumentException("inpustream is null"); }
		this.name = name;
		this.contentType = Utils.trimOrDefaultIfBlank( contentType, ()->MIMETYPES.getContentType( name ) );
		this.inputStream = inputStream;
	}
	
	public Attachment(String name, String contentType, URL url) throws IOException {
		this( name, contentType, url.openStream() );
	}
	
	public Attachment(String name, String contentType, byte[] bytes) {
		this( name, contentType, new ByteArrayInputStream( bytes ) );
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}
	
}
