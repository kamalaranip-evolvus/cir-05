/**
 * 
 */

package com.evolvus.dds.h2hconnectweb.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.api.management.ManagedOperation;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.support.ServiceSupport;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.evolvus.dds.h2hconnectweb.domain.FileStore;

/**
 * @author rojalinb
 * 
 */
public class DataTableIdempotentRepository extends ServiceSupport implements IdempotentRepository<String> {

	private static final Logger LOG = LoggerFactory.getLogger(DataTableIdempotentRepository.class);

	private AtomicBoolean init = new AtomicBoolean();

	private String fileType;

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
	 * Default Constructor
	 */
	public DataTableIdempotentRepository() {
		super();
	}

	/**
	 * @param fileType
	 */
	public DataTableIdempotentRepository(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @param fileType
	 * @return
	 */
	public static IdempotentRepository<String> dataTableIdempotentRepository(String fileType) {
		return new DataTableIdempotentRepository(fileType);
	}

	@Autowired(required = true)
	private FileStoreService fileStoreService;

	/**
	 * @return
	 */
	public FileStoreService getFileStoreService() {
		return fileStoreService;
	}

	/**
	 * @param fileStoreService
	 */
	public void setFileStoreService(FileStoreService fileStoreService) {
		this.fileStoreService = fileStoreService;
	}

	/**
	 * add
	 */
	@ManagedOperation(description = "Adds the key to the store")
	public boolean add(String key) {
		LOG.debug("key value == " + key);
		if (fileStoreService.containsKey(key, fileType)) {
			return false;
		} else {
			saveToTable(key, fileType);
			return true;
		}
	}

	/**
	 * contains
	 */
	@ManagedOperation(description = "Does the store contain the given key")
	public boolean contains(String key) {
		return fileStoreService.containsKey(key, fileType);

	}

	/**
	 * remove
	 */
	public boolean remove(String key) {
		return false;
	}

	/**
	 * confirm
	 */
	public boolean confirm(String key) {
		return false;
	}

	protected void saveToTable(final String messageId, String fileType) {
		FileStore fileStoreObj = new FileStore();
		fileStoreObj.setFileName(messageId);
		fileStoreObj.setFileType(fileType);
		fileStoreObj.setFileDate(new Date());
		try {
			fileStoreService.save(fileStoreObj);
		} catch (Exception e) {
			LOG.error("{}" + e);
			throw ObjectHelper.wrapRuntimeCamelException(e);
		} finally {
			LOG.debug("Saving to Table idempotent repository");
		}
	}

	@Override
	protected void doStart() throws Exception {
		init.compareAndSet(false, true);
	}

	@Override
	protected void doStop() throws Exception {
		init.set(false);
	}

	/**
	 * Clear the repository.
	 */
	public void clear() {
		dataTableIdempotentRepository(fileType).clear();
	}

}
