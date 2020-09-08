package com.evolvus.dds.h2hconnectweb.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.apache.commons.io.FileUtils;

public class PGPDecryptionProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(PGPDecryptionProcessor.class);

	@Value("${pgp_private_path}")
	private String privateKeypath;

	@Value("${pgp_public_path}")
	private String publickpath;

	@Value("${npci_pivateketstore_path}")
	private String privatekeyStore;

	@Value("${npci_pivateketstore_password}")
	private String password;

	@Value("${npci_pivateketstore_alias}")
	private String alias;
	
	@Value("${actualPath}")
	private String actualPath;

	public void process(Exchange arg0) throws Exception {
//		String filePath = (String) arg0.getIn().getHeader("filePath");
		
		try {
			
			Security.addProvider(new BouncyCastleProvider());
			
			String fileName = (String) arg0.getIn().getHeader("fileName");
			LOG.info("FILENAME FOR DECRYPT:::::::::::::::" + fileName);
			String filePath = (String) arg0.getIn().getHeader("filePath");
			LOG.info("FILEPATH FILENAME FOR DECRYPT:::::::::::::::" + filePath);
			File file = new File(actualPath+filePath);
			LOG.info("FILE FILENAME FOR DECRYPT::::::::::::::::" + file);

			PrivateKey pvtKey = this.privateFromKeyStore(privatekeyStore, password, alias);

			BufferedReader br = new BufferedReader(new FileReader(file));

//			String contentRead;
//			String content = null;
//			while ((contentRead = br.readLine()) != null) {
//				content = contentRead;
//			}
//			LOG.info(content);

			FileInputStream finFile = new FileInputStream(file);

			PGPPublicKey pgppub = this.extractPubKey(publickpath);
			
			LOG.info("PUBLIC KEY::::::::::::::"+ pgppub);

//			FileInputStream privateKeyIn = new FileInputStream(privateKeypath);
				

			PGPPrivateKey privateKeyIn = (new JcaPGPKeyConverter().getPGPPrivateKey(pgppub, pvtKey));

			this.rsaDecryptFile(finFile, file, privateKeyIn);

		} catch (Exception e) {
			LOG.error(ExceptionUtils.getFullStackTrace(e));
		}

	}

	public void rsaDecryptFile(InputStream in, File fileout, PGPPrivateKey priK) {
		try {
			Security.addProvider(new BouncyCastleProvider());

			in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
			PGPObjectFactory pgpF = new PGPObjectFactory(in);
			PGPEncryptedDataList enc;
			Object o = pgpF.nextObject();
			//
			// the first object might be a PGP marker packet.
			//
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			} else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			//
			// find the secret key
			//
			Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;

			while (sKey == null && it.hasNext()) {
				pbe = it.next();
				// sKey = findSecretKey(priKstream, pbe.getKeyID(), passparse.toCharArray());
				sKey = priK;
			}

			if (sKey == null) {
				throw new IllegalArgumentException("Secret key for message not found.");
			}

			PublicKeyDataDecryptorFactory b = new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC")
					.setContentProvider("BC").build(sKey);

//			InputStream clear = pbe.getDataStream(b);

			InputStream clear = pbe.getDataStream(sKey, "BC");

			PGPObjectFactory plainFact = new PGPObjectFactory(clear);

			Object message = plainFact.nextObject();
			System.out.println("Secret key info 3:: " + pbe.getKeyID() + new Date());
			if (message instanceof PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream());

				message = pgpFact.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;
				InputStream unc = ld.getInputStream();
				int ch;

				FileUtils.copyInputStreamToFile(unc, new File(fileout.toString()));

			} else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException("Encrypted message contains a signed message - not literal data.");
			} else {
				throw new PGPException("Message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected()) {
				if (!pbe.verify()) {
					throw new PGPException("Message failed integrity check");
				}
			}

		} catch (PGPException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public PGPPublicKey extractPubKey(String publicKeyPath) {
		PGPPublicKey key = null;
		try {

			FileInputStream fin = new FileInputStream(publicKeyPath);
			InputStream in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(fin);

			PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

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
			LOG.error(ExceptionUtils.getFullStackTrace(e));
		}

		return key;

	}

	public PrivateKey privateFromKeyStore(String privatekeystorePath, String certpassword, String certalias) {
		PrivateKey privateKey = null;
		try {

			FileInputStream fins = new FileInputStream(privatekeystorePath);

			KeyStore ks = KeyStore.getInstance("JKS");

			ks.load(fins, certpassword.toCharArray());
			KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(certalias,
					new KeyStore.PasswordProtection(certpassword.toCharArray()));

			privateKey = keyEntry.getPrivateKey();

		} catch (Exception e) {

			LOG.error(ExceptionUtils.getFullStackTrace(e));

		}

		return privateKey;

	}

}
