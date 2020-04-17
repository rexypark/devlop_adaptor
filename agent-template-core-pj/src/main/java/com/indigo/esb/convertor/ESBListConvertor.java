package com.indigo.esb.convertor;

import java.util.List;

public interface ESBListConvertor<T> {
	
	public List<T> convert(Object data) throws Exception; 
  
}