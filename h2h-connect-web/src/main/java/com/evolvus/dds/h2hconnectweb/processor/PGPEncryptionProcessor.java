package com.evolvus.dds.h2hconnectweb.processor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.MalformedURLException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.NoSuchProviderException;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.SecureRandom;
//import java.security.Security;
//import java.util.Date;
//import java.util.Iterator;
//
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.io.FileUtils;
//import org.bouncycastle.bcpg.ArmoredOutputStream;
//import org.bouncycastle.cms.CMSProcessableByteArray;
//import org.bouncycastle.cms.CMSSignedData;
//import org.bouncycastle.cms.CMSTypedData;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.openpgp.PGPCompressedData;
//import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
//import org.bouncycastle.openpgp.PGPEncryptedData;
//import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
//import org.bouncycastle.openpgp.PGPEncryptedDataList;
//import org.bouncycastle.openpgp.PGPException;
//import org.bouncycastle.openpgp.PGPLiteralData;
//import org.bouncycastle.openpgp.PGPObjectFactory;
//import org.bouncycastle.openpgp.PGPOnePassSignatureList;
//import org.bouncycastle.openpgp.PGPPrivateKey;
//import org.bouncycastle.openpgp.PGPPublicKey;
//import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
//import org.bouncycastle.openpgp.PGPPublicKeyRing;
//import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
//import org.bouncycastle.openpgp.PGPSecretKey;
//import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
//import org.bouncycastle.openpgp.PGPUtil;
//import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
//import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;
//import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
//import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
//import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.stereotype.Service;



















public class PGPEncryptionProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(PGPEncryptionProcessor.class);

	@Value("${npci_public_path}")
	private String npcipublickpath;

	@Value("${npci_pivateketstore_path}")
	private String privatekeyStore;

	@Value("${npci_pivateketstore_password}")
	private String password;

	@Value("${npci_pivateketstore_alias}")
	private String alias;

	@Value("${pgp_public_path}")
	private String publickpath;

	public void process(Exchange arg0) throws Exception {

//	 		String filePath = (String) arg0.getIn().getHeader("filePath");
		
		try {
			Security.addProvider(new BouncyCastleProvider());

		String fileName = (String) arg0.getIn().getHeader("fileName");
		LOG.info("FILENAME:::::::::::::::" + fileName);
		String filePath = (String) arg0.getIn().getHeader("filePath");
		LOG.info("FILEPATH:::::::::::::::" + filePath);
		File file = new File(filePath); 
		LOG.info("FILE::::::::::::::::"+file);
		
//		 BufferedReader br = new BufferedReader(new FileReader(file));
//		 
//		 String contentRead ;
//		 String content = null; 
//		  while ((contentRead = br.readLine()) != null) {
//		    content=contentRead; 
//		  } 
//		  LOG.info(content);
		  
//		FileOutputStream fout = new FileOutputStream(content);
		  
		  FileOutputStream fout = new FileOutputStream(filePath);

		PublicKey pk = this.returnPublickKey(npcipublickpath);
		
		PGPPublicKey  pgppub=	(new JcaPGPKeyConverter().getPGPPublicKey(PGPPublicKey.RSA_GENERAL, pk, new Date()));

//		PGPPublicKey pgppub = this.extractPubKey(publickpath);

		byte[] byteMessageEnc = this.rsaEncryptFile(fout, filePath, pgppub, false, false);

//		arg0.getOut().setHeader("filePath", filePath);
//		arg0.getOut().setBody(byteMessageEnc.toString());
		
		writeFile(byteMessageEnc.toString(), fileName);
		
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getFullStackTrace(e));
		}

	}

	public PublicKey returnPublickKey(String publicKeyPath) throws CertificateException, IOException {

		PublicKey publicKey = null;
		Security.addProvider(new BouncyCastleProvider());
		try {
			FileInputStream fin = new FileInputStream(publicKeyPath);
			CertificateFactory f = CertificateFactory.getInstance("X.509");

			X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);

			publicKey = certificate.getPublicKey();

//				LOG.debug("publicKey::::::::::"+publicKey);

		} catch (Exception e) {
			LOG.error(ExceptionUtils.getFullStackTrace(e));
		}
		return publicKey;

	}

	public PGPPublicKey extractPubKey(String publicKeyPath) {
		PGPPublicKey key = null;
		try {

			FileInputStream fin = new FileInputStream(publicKeyPath);
			InputStream in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(fin);

			PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

			//
			// we just loop through the collection till we find a key suitable for
			// encryption, in the real
			// world you would probably want to be a bit smarter about this.
			//

			//
			// iterate through the key rings.
			//
			Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();

			while (key == null && rIt.hasNext()) {
				PGPPublicKeyRing kRing = rIt.next();
				Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
				while (key == null && kIt.hasNext()) {
					PGPPublicKey k = kIt.next();

					if (k.isEncryptionKey()) {
						key = k;
					}
				}
			}

			if (key == null) {
				throw new IllegalArgumentException("Can't find encryption key in key ring.");
			}

		} catch (Exception e) {

			e.printStackTrace();
			// log.error(ExceptionUtils.getFullStackTrace(e));
		}

		return key;

	}

	public byte[] rsaEncryptFile(OutputStream out, String filePath, PGPPublicKey encKey, boolean armor,
			boolean withIntegrityCheck) {

		try {

			if (armor) {
				out = new ArmoredOutputStream(out);

			}
			LOG.info("FILEPATH in RSA_ENC;;;;;;;;;;;;" + filePath);
			File file = new File(filePath);
			LOG.info("FILE in RSA_ENC;;;;;;;;;;;;" + file);
			BufferedReader br = new BufferedReader(new FileReader(file));
			LOG.info("BR::::::"+br);
			String contentRead;
			String content = null;
			while ((contentRead = br.readLine()) != null) {
				content = contentRead;
			}

			LOG.info("CONTENT::::::::::::::::" + content);

			ByteArrayOutputStream bOut = new ByteArrayOutputStream();

			PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

			PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(filePath));

			comData.close();

			JcePGPDataEncryptorBuilder c = new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
					.setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(new SecureRandom()).setProvider("BC");

			PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(c);

			JcePublicKeyKeyEncryptionMethodGenerator d = new JcePublicKeyKeyEncryptionMethodGenerator(encKey)
					.setProvider(new BouncyCastleProvider()).setSecureRandom(new SecureRandom());

			cPk.addMethod(d);
			
//			PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5, withIntegrityCheck,
//					new SecureRandom(), "BC");
//			cPk.addMethod(encKey);
//
			byte[] bytes = bOut.toByteArray();

			OutputStream cOut = cPk.open(out, bytes.length);

			cOut.write((bytes));
//				  bw.write(Base64.getEncoder().encodeToString(bytes));
//				  FileUtils.copyFile(new File("D:\\Development_Docs\\PGP\\Bulk\\plain-text-common.pgp"), cOut);

			cOut.close();
			out.close();
			// bw.close();

			return bytes;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;

	}
	
	public void writeFile(final String content, final String fileName) {
		try{
			FileWriter fileWriter = new FileWriter(fileName);
			fileWriter.write(content);
			fileWriter.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
