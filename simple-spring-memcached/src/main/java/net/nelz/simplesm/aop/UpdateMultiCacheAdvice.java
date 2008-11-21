package net.nelz.simplesm.aop;

import net.nelz.simplesm.annotations.*;
import org.apache.commons.logging.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

import java.lang.reflect.*;
import java.util.*;

/**
Copyright (c) 2008  Nelson Carpentier

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
@Aspect
public class UpdateMultiCacheAdvice extends CacheBase {
	private static final Log LOG = LogFactory.getLog(UpdateMultiCacheAdvice.class);

	@Pointcut("@annotation(net.nelz.simplesm.annotations.UpdateMultiCache)")
	public void updateSingle() {}

	@AfterReturning(pointcut="updateSingle()", returning="retVal")
	public Object cacheUpdateSingle(final JoinPoint jp, final Object retVal) throws Throwable {
		try {
			final Method methodToCache = getMethodToCache(jp);
			final UpdateMultiCache annotation = methodToCache.getAnnotation(UpdateMultiCache.class);
			validateAnnotation(annotation, methodToCache);

		} catch (Exception ex) {
			LOG.warn("Updating caching via " + jp.toShortString() + " aborted due to an error.", ex);
		}
		return retVal;
	}

	protected List<String> getObjectIds(final int keyIndex,
	                             final Object returnValue,
	                             final JoinPoint jp,
	                             final Method methodToCache) throws Exception {
		final Object keyObject = keyIndex == -1
									? validateReturnValueAsKeyObject(returnValue, methodToCache)
									: getKeyObject(keyIndex, jp, methodToCache);
		final Method keyMethod = getKeyMethod(keyObject);
		return generateObjectId(keyMethod, keyObject);
	}

	protected void validateAnnotation(final UpdateMultiCache annotation,
	                                  final Method method) {

		final Class annotationClass = UpdateMultiCache.class;
		validateAnnotationExists(annotation, annotationClass);
		validateAnnotationIndex(annotation.keyIndex(), true, annotationClass, method);
		validateAnnotationNamespace(annotation.namespace(), annotationClass, method);
		validateAnnotationExpiration(annotation.expiration(), annotationClass, method);
	}

}
