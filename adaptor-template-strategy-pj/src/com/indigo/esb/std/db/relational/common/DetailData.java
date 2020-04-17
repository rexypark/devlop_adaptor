package com.indigo.esb.std.db.relational.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DetailData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private List<Map<String, Object>> detailList = new ArrayList<Map<String,Object>>();
	
	public List<Map<String, Object>> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<Map<String, Object>> detailList) {
		this.detailList = detailList;
	}
	

	@Override
	public String toString() {

		class ToStringStyleCustom extends ToStringStyle {

			private static final long serialVersionUID = 1L;

			public ToStringStyleCustom() {

				super.setNullText("[null]");
				this.setContentStart("[");
				this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
				this.setFieldSeparatorAtStart(true);
				this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
			}
		}

		return ReflectionToStringBuilder.toString(this, new ToStringStyleCustom());
	}
	
	
}
