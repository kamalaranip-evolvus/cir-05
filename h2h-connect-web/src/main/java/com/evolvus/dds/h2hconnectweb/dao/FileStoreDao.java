/**
 * 
 */

package com.evolvus.dds.h2hconnectweb.dao;
import java.util.List;

import com.evolvus.dds.h2hconnectweb.domain.FileStore;

/**
 * @author rojalinb
 *
 */
public interface FileStoreDao {
	
	List<FileStore> findAll();
	void save(FileStore fileStore);
	FileStore findById(Long fileId);
	void delete(FileStore fileStore);
	void update(FileStore fileStore);
	
	boolean containsKey(String fileKey, String fileType);
}
