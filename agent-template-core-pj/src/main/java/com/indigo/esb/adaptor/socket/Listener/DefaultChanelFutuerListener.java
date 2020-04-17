package com.indigo.esb.adaptor.socket.Listener;

import java.io.File;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.fileserver.comm.IndigoFileTransferUtil;


public class DefaultChanelFutuerListener implements ChannelFutureListener{
      protected final Logger         log = LoggerFactory.getLogger(getClass());
      private String filePath;
      private String fileName;
      public DefaultChanelFutuerListener(){


      }

      public DefaultChanelFutuerListener(String filePath, String fileName){
            this.filePath = filePath;
            this.fileName = fileName;
      }

      @Override
      public void operationComplete( ChannelFuture future ) throws Exception{
            File targetFile = new File(filePath + File.separator + fileName);
            String metaData = IndigoFileTransferUtil.getMetaDataXmlForPut("", targetFile);
            ChannelBuffer bf = ChannelBuffers.dynamicBuffer();
            bf.writeBytes(metaData.getBytes());
            bf.writeByte(0);

            future.getChannel().write(bf);
            future.getChannel().write(new ChunkedFile(targetFile, 8192)).addListener(new ChannelFutureListener(){
                  @Override
                  public void operationComplete( ChannelFuture future ) throws Exception{
                        log.info("ESB Socket Server File Write !!!");
                  }
            });
            future.getChannel().getCloseFuture().addListener(new ChannelFutureListener(){
                  @Override
                  public void operationComplete( ChannelFuture future ) throws Exception{
                        log.info("ESB Socket Server Connect Close !!!");
                  }
            });

            log.debug("tempDir file remove :" + filePath);
            deleteDirectory(new File(filePath));
      }
      public boolean deleteDirectory(File path) {
            if(!path.exists()) {
                return false;
            }

            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
            return path.delete();
        }
}
