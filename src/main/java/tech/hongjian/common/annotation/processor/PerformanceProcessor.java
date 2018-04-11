package tech.hongjian.common.annotation.processor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import tech.hongjian.common.annotation.Performance;

/**
 * @author xiahongjian
 * @time 2018-04-11 11:05:59
 *
 */
@Aspect
public class PerformanceProcessor {
	private static final Logger logger = LoggerFactory.getLogger(PerformanceProcessor.class);
	private boolean isOpen;
	
	public PerformanceProcessor() {
		this(false);
	}
	
	public PerformanceProcessor(boolean isOpen) {
		this.isOpen = isOpen;
	}

	@Around("@annotation(performance)")
	public Object process(ProceedingJoinPoint joinPoint, Performance performance) throws Throwable {
		// 未开启performance注解，直接返回方法结果
		if (!isOpen)
			return joinPoint.proceed();
		LocalTime before = LocalTime.now();
		Object result = joinPoint.proceed();
		LocalTime after = LocalTime.now();
		Duration used = Duration.between(before, after);
		logTimeUsed(performance.level(), generateLogContent(methodFullName(joinPoint), used, getTimeUnit(performance)));
		return result;
	}

	private String methodFullName(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		return signature.getDeclaringTypeName() + "." + signature.getName();
	}

	private String generateLogContent(String methodName, Duration uesd, TimeUnit unit) {
		return "[" + methodName + "] runed " + getDurationValue(uesd, unit) + "(" + unit.name() + ").";
	}

	private TimeUnit getTimeUnit(Performance performance) {
		TimeUnit unit = performance.value();
		if (unit == TimeUnit.MILLISECONDS || unit == TimeUnit.SECONDS || unit == TimeUnit.MINUTES)
			return unit;
		return TimeUnit.MILLISECONDS;
	}

	private long getDurationValue(Duration duration, TimeUnit unit) {
		if (unit == TimeUnit.SECONDS)
			return duration.getSeconds();
		if (unit == TimeUnit.MINUTES)
			return duration.toMinutes();
		return duration.toMillis();
	}

	private void logTimeUsed(Level level, String log) {
		if (level == Level.ERROR)
			logger.debug(log);
		if (level == Level.INFO)
			logger.info(log);
		if (level == Level.TRACE)
			logger.trace(log);
		if (level == Level.WARN)
			logger.warn(log);
		logger.debug(log);
	}
}
