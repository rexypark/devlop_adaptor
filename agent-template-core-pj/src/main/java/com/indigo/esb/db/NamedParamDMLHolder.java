package com.indigo.esb.db;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NamedParamDMLHolder {
	String select;
	String update;
	String delete;
	String insert;
	String merge;

	String sourceUpdate;
	String sourceResultUpdate;

	public NamedParamDMLHolder() {
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

	public String getInsert() {
		return insert;
	}

	public void setInsert(String insert) {
		this.insert = insert;
	}

	public String getSourceUpdate() {
		return sourceUpdate;
	}

	public void setSourceUpdate(String sourceUpdate) {
		this.sourceUpdate = sourceUpdate;
	}

	public String getSourceResultUpdate() {
		return sourceResultUpdate;
	}

	public void setSourceResultUpdate(String sourceResultUpdate) {
		this.sourceResultUpdate = sourceResultUpdate;
	}

	public String getMerge() {
		return merge;
	}

	public void setMerge(String merge) {
		this.merge = merge;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
