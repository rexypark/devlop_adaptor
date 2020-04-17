package com.indigo.esb.std.db.relational.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DataSet implements Serializable{
	private static final long serialVersionUID = 1395589228084171831L;

	private Map<String, Object> mainData;
	
	//private List<DetailData> detailList = new ArrayList<DetailData>();
	
	private Map<String, Object> detailMap = new LinkedHashMap<String, Object>();
	
	public Map<String, Object> getMainData() {
		return mainData;
	}

	public void setMainData(Map<String, Object> mainData) {
		this.mainData = mainData;
	}
	
	public Map<String, Object> getDetailList() {
		return detailMap;
	}

	public void setDetailList(Map<String, Object> detailList) {
		this.detailMap = detailList;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detailMap == null) ? 0 : detailMap.hashCode());
		result = prime * result + ((mainData == null) ? 0 : mainData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSet other = (DataSet) obj;
		if (detailMap == null) {
			if (other.detailMap != null)
				return false;
		} else if (!detailMap.equals(other.detailMap))
			return false;
		if (mainData == null) {
			if (other.mainData != null)
				return false;
		} else if (!mainData.equals(other.mainData))
			return false;
		return true;
	}
}
