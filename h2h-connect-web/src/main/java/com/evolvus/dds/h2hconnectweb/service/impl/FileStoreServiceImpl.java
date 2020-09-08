/**
 * 
 */
package com.evolvus.dds.h2hconnectweb.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.evolvus.dds.h2hconnectweb.dao.FileStoreDao;
import com.evolvus.dds.h2hconnectweb.domain.FileStore;
import com.evolvus.dds.h2hconnectweb.service.FileStoreService;

/**
 * @author rojalinb
 * 
 */
@Component
@Transactional(readOnly=true)
public class FileStoreServiceImpl implements FileStoreService {

	@Autowired(required = true)
	private FileStoreDao fileStoreDao;

	public List<FileStore> findAll() {
		return null;
	}

	@Transactional
	public void save(FileStore fileStore) {
		fileStoreDao.save(fileStore);
	}

	public FileStore findById(Long fileId) {
		return fileStoreDao.findById(fileId);
	}

	@Transactional
	public void delete(FileStore fileStore) {
		fileStoreDao.delete(fileStore);
	}

	@Transactional
	public void update(FileStore fileStore) {
		fileStoreDao.update(fileStore);

	}

	public boolean containsKey(String fileKey, String fileType) {
		return fileStoreDao.containsKey(fileKey, fileType);
	}

}
