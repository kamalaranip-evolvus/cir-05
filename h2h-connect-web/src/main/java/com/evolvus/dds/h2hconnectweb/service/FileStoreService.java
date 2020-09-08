/**
 * 
 */
package com.evolvus.dds.h2hconnectweb.service;

import java.util.List;

import com.evolvus.dds.h2hconnectweb.domain.FileStore;

/**
 * @author rojalinb
 * 
 */
public interface FileStoreService {
	/**
	 * 
	 * @return
	 */
	List<FileStore> findAll();

	/**
	 * @param fileStore
	 */
	void save(FileStore fileStore);

	/**
	 * @param fileId
	 * @return
	 */
	FileStore findById(Long fileId);

	/**
	 * @param fileStore
	 */
	void delete(FileStore fileStore);

	/**
	 * @param fileStore
	 */
	void update(FileStore fileStore);
	
	/**
	 * @param fileKey
	 * @param fileType
	 * @return
	 */
	boolean containsKey(String fileKey, String fileType);
}