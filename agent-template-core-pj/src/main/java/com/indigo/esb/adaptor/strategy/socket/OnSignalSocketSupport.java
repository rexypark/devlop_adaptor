package com.indigo.esb.adaptor.strategy.socket;

import java.io.File;

import net.sf.ehcache.Ehcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.socket.client.FileServerClientManager;
import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.util.ZipUtils;

/**
 *
 *
 * @author yoonjonghoon
 *
 */
public abstract class OnSignalSocketSupport implements OnSignalStrategy, InitializingBean{

      protected final Logger            log = LoggerFactory.getLogger(getClass());
      @Autowired(required = false)
      protected JmsTemplate             jmsTemplate;
      @Autowired(required = false)
      protected FileServerClientManager fileServerManager;
      @Autowired(required = false)
      protected Ehcache                 clientCache;
      @Autowired(required = false)
      protected Ehcache                 channelCache;

      public OnSignalSocketSupport() {

      }

      public boolean sendFile( String sendDir, String tx_id, boolean delFlag ) throws Exception{
            log.debug("############## tempDir proccess........................");
            File tempDir = new File(sendDir);

            File[] zipFileList = tempDir.listFiles();
            ZipUtils.compress(zipFileList, tempDir.getAbsolutePath(), new File(sendDir + File.separator + tx_id + ".zip"));
            log.info("=========# make zip file end =======");
            if (fileServerManager.sendSocket(sendDir, tx_id + ".zip")){
                  log.info("ESB FileServer Send Success");
                  log.debug("ZipFile Name :"+ tx_id+".zip");
                  if (delFlag){
                        new File(sendDir, tx_id + ".zip").delete();
                  }
                  return true;
            }
            return false;
      }

      @Override
      public void afterPropertiesSet() throws Exception{

      }

}
