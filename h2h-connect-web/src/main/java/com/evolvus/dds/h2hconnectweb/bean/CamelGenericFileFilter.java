/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolvus.dds.h2hconnectweb.bean;

import java.util.Calendar;
import java.util.Date;

import org.apache.camel.component.file.AntPathMatcherFileFilter;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.springframework.beans.factory.annotation.Value;

/**
 * File filter using AntPathMatcher.
 * <p/>
 * Exclude take precedence over includes. If a file match both exclude and
 * include it will be regarded as excluded.
 * 
 * @param <T>
 */
public class CamelGenericFileFilter<T> implements GenericFileFilter<T> {

	private final AntPathMatcherFileFilter filter;

	@Value("${sftp.day.diff}")
	private int sftpDayDiff;

	public int getSftpDayDiff() {
		return sftpDayDiff;
	}

	public void setSftpDayDiff(int sftpDayDiff) {
		this.sftpDayDiff = sftpDayDiff;
	}

	/**
	 * Default Constructor
	 */
	public CamelGenericFileFilter() {
		filter = new AntPathMatcherFileFilter();
	}

	/**
	 * 
	 * @param includes
	 */
	public CamelGenericFileFilter(String... includes) {
		filter = new AntPathMatcherFileFilter();
		filter.setIncludes(includes);
	}

	/**
	 * accept
	 */
	public boolean accept(GenericFile<T> file) {
		// directories should always be accepted by ANT path matcher
		if (file.isDirectory()) {
			return true;
		}

		String path = file.getRelativeFilePath();
		boolean resutl1 = filter.acceptPathName(path);
		boolean result2 = false;

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -sftpDayDiff);
		cal.set(Calendar.HOUR_OF_DAY, 00);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);
		Date date = cal.getTime();

		Date fileDate = new Date(file.getLastModified());

		result2 = fileDate.compareTo(date) >= 1;

		return result2 & resutl1;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getExcludes() {
		return filter.getExcludes();
	}

	/**
	 * 
	 * @param excludes
	 */
	public void setExcludes(String[] excludes) {
		filter.setExcludes(excludes);
	}

	/**
	 * 
	 * @return
	 */
	public String[] getIncludes() {
		return filter.getIncludes();
	}

	/**
	 * 
	 * @param includes
	 */
	public void setIncludes(String[] includes) {
		filter.setIncludes(includes);
	}

	/**
	 * Sets excludes using a single string where each element can be separated
	 * with comma
	 */
	public void setExcludes(String excludes) {
		filter.setExcludes(excludes);
	}

	/**
	 * Sets includes using a single string where each element can be separated
	 * with comma
	 */
	public void setIncludes(String includes) {
		filter.setIncludes(includes);
	}

	/**
	 * Sets case sensitive flag on
	 * {@link org.apache.camel.component.file.AntPathMatcherFileFilter}
	 * <p/>
	 * Is by default turned on <tt>true</tt>.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		filter.setCaseSensitive(caseSensitive);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isCaseSensitive() {
		return filter.isCaseSensitive();
	}
}
