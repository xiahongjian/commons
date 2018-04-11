package tech.hongjian.commons.annotation.processor;

import tech.hongjian.commons.model.WebLogInfo;

/**
 * @author xiahongjian 
 * @time   2018-04-11 15:36:44
 *
 */
@FunctionalInterface
public interface LogService {
	void log(WebLogInfo info);
}
