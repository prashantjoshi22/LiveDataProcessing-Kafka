//Code to read the data from the file continuously.
//By Prashant Joshi.


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;




public class Kafka_producer {

	public static void main(String [] args) throws IOException, InterruptedException
	{
		long presentLength =0;
		long lengthBefore =0;
		String line =null;
		
		Properties props = new Properties();
		
		props.put("zk.connect","localhost:2181");
		props.put("serializer.class","kafka.serializer.StringEncoder");
		props.put("metadata.broker.list","localhost:9092");
		props.put("request.required.acks","1");
		
		ProducerConfig producer_config = new ProducerConfig(props);
		
		Producer p = new Producer(producer_config);
		
		File f =new File("/home/prashant/Desktop/f1");
			
		while(true)  
			{
			
			RandomAccessFile raf = null;
				if((presentLength = f.length()) > lengthBefore)
					{
				raf	=new  RandomAccessFile(f,"r"); //here "r" argument stands for Read operation .
					raf.seek(lengthBefore);        //seek will set the pointer to that position from where next read will occur.
					lengthBefore =presentLength;
					while((line =raf.readLine()) != null)
						{
						
						p.send(new KeyedMessage("Reading_logs",line));	// This will send the data to Kafka Broker.
						}
					
					}
				
				
			}
		
	}
	
}