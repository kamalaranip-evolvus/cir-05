/**
 * 
 */

package com.evolvus.dds.h2hconnectweb.dao.impl;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.evolvus.dds.h2hconnectweb.dao.FileStoreDao;
import com.evolvus.dds.h2hconnectweb.domain.FileStore;

/**
 * @author rojalinb
 * 
 */
@Repository
public class FileStoreDaoImpl extends AbstractDaoImpl<FileStore> implements
		FileStoreDao {

	private static final Logger LOG = LoggerFactory
			.getLogger(FileStoreDaoImpl.class);

	/**
	 * Default Constructor.
	 */
	public FileStoreDaoImpl() {
		super();
		setClazz(FileStore.class);
	}

	public boolean containsKey(String fileKey, String fileType) {
		boolean result = false;
		Query query = entityManager
				.createQuery(" from "
						+ FileStore.class.getName()
						+ " fileStore where fileStore.fileName = :fileName and fileStore.fileType = :fileType");
		query.setParameter("fileName", fileKey);
		query.setParameter("fileType", fileType);
		if (query.getResultList().size() > 0) {
			result = true;
		}
		LOG.debug("result ======= " + result);
		return result;
	}
}
