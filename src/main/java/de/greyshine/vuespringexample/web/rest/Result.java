package de.greyshine.vuespringexample.web.rest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greyshine.vuespringexample.utils.Utils;

public class Result<T> {
	
	private static volatile long ids = 0;
	public final String id = Long.toString( ++ids , 36);
	
	public final String time = Utils.toString( LocalDateTime.now() );
	public Long total = null;
	public final List<T> data = new ArrayList<>();
	
	public Result() {}
	
	public Result(List<T> datas) {
		data( datas );
	}
	
	public Result(Iterable<T> dataIterator) {
		
		total = 0L;
		
		if ( dataIterator != null ) {

			dataIterator.forEach( item -> {
				data.add( item );
				total++;
			} );	
		}
	}
	
	public Result<T> data(T data) {
		return data( data, data == null ? null : 1L );
	}
	
	public Result<T> data(T data, Long totalSize) {
		
		if ( data != null ) {
			this.data.add( data );			
		}
		total = totalSize;
				
		return this;
	}
	
	public Result<T> data(List<T> datas) {
		return data( datas, datas == null ? 0 : (long)datas.size() );
	}
	
	public Result<T> data(List<T> datas, Long totalSize) {
		
		this.data.addAll( datas != null ? datas : Collections.emptyList() );
		total = totalSize;
		
		return this;
	}
	
}
