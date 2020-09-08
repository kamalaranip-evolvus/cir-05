/**
 * 
 */

package com.evolvus.dds.h2hconnectweb.domain;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author rojalinb
 * 
 */
@Entity
@Table(name = "flx_file_store")
public class FileStore implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5253362121388670732L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "flx_file_store_seq")
	@SequenceGenerator(name = "flx_file_store_seq", sequenceName = "FLX_FILE_STORE_SEQ")
	@Column(name = "ID")
	private Long fileId;
	@Column(name = "FILE_NAME")
	private String fileName;
	@Column(name = "FILE_TYPE")
	private String fileType;

	@Column(name = "FILE_DTTIME")
	private Date fileDate;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the fileId
	 */
	public Long getFileId() {
		return fileId;
	}

	/**
	 * @param fileId
	 *            the fileId to set
	 */
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	
	/**
	 * @return the fileDate
	 */
	public Date getFileDate() {
		Date result = null;
		if (fileDate != null) {
			result = (Date) fileDate.clone();
		}
		return result;
	}

	/**
	 * @param fileDate
	 *            the fileDate to set
	 */
	public void setFileDate(Date fileDate) {
		this.fileDate = (Date) fileDate.clone();
	}
}
