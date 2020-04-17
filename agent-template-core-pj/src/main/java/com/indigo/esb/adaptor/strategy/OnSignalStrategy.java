package com.indigo.esb.adaptor.strategy;

import com.indigo.esb.adaptor.context.IndigoSignalResult;

public interface OnSignalStrategy {

	void onStart(IndigoSignalResult onSignalResult) throws Exception;
}
