package com.dubture.symfony.core.model.listener;

/**
 * 
 * Interface for listening to model clear events.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public interface IModelClearListener {
	
	
	/**
	 * Will be called when the model is completely
	 * wiped during a full-build.
	 * 
	 */
	public void modelCleared();

}
