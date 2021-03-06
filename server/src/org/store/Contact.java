package org.store;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.bean.ContactInfo; 
/**
 * 封装联系人
 * @author xqs
 *
 */
public class Contact {
	private Configuration conf = null;  
	public Contact() {
		conf = HBaseConfiguration.create();
		conf.set("hbase.master", Constants.HostName+":60000");
		conf.set("hbase.zookeeper.quorum", Constants.HostName);
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		
	} 
	
	public void storeToHbase(String userName ,String jsonData) throws Exception{ 
		HBaseUtil hu=new HBaseUtil(); 
		hu.creatTable(Constants.TableName,Constants.Familys);
		List<ContactInfo> data=hu.parseContactJsonData(jsonData);
		List<ContactInfo> data2=getContactList(userName);
		boolean have;
		int size=data2.size();
		for(int i=0;i<data.size();i++)
		{
			ContactInfo contactInfo=data.get(i); 
			have=false;
			if(data2!=null){
				for(ContactInfo contactInfo2:data2){
					if(contactInfo.getName().equals(contactInfo2.getName())&&contactInfo.getPhone().equals(contactInfo2.getPhone())){
						have=true;
						break;
					}
				}
			} 
			if(!have){
				String id=hu.getValue(Constants.PublicKey,Constants.Family,Constants.PublicId);
				if(id==null){
					id="0";
				}
				id=Utils.zhuanhuan(Integer.parseInt(id)+1);
				hu.addRecord(userName+id,Constants.Family,Constants.ColumnPrefix2+"name", contactInfo.getName());
				hu.addRecord(userName+id,Constants.Family,Constants.ColumnPrefix2+"phone", contactInfo.getPhone());
				hu.addRecord(Constants.PublicKey,Constants.Family,Constants.PublicId,id);
				
			} 
		}  
	}
	
	public String getContactJsonData(String userName){
		HBaseUtil hu=new HBaseUtil(); 
		return hu.getRecords(userName,Constants.ColumnPrefix2);
	}
	
	public List<ContactInfo>  getContactList(String userName){
		HBaseUtil hu=new HBaseUtil();
		return hu.parseContactJsonData(getContactJsonData(userName));
	}
}
