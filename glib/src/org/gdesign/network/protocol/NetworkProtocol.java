package org.gdesign.network.protocol;


import java.io.Serializable;
import java.util.UUID;

public class NetworkProtocol implements Serializable {

	private static final long 		serialVersionUID 	= 7887287159898601239L;
	
	private final static int		INITIALIZE			=  0xFF;
	public final static int 		COMMAND				=  0x00;
	public final static int 		MESSAGE				=  0x01;
	public final static int 		STATUS				=  0x02;
	public final static int 		OBJECT				=  0x03;
	
	public final static int			CMD_AUTH_WL			=  0x000001;
	public final static int			CMD_AUTH_PASS		=  0x000002;
	public final static int			CMD_CONN_TERM		=  0x000010;
		
	public static final String 		AUTHENTICATION_SUCCESSFUL 	= "$AUTHOK$";
	public static final String 		AUTHENTICATION_ERROR 		= "$AUTHERR$";
	
	public static int 				TIMEOUT				= 600000;
	
	private int 					type 				= INITIALIZE;
	private int 					status 				= INITIALIZE;
	private int 			 		command 			= INITIALIZE;
	private UUID					packageid			= null;
	private String 					username 			= null;
	private String					message 			= null;
	private Object					object 				= null;

	
	public NetworkProtocol(int type, String username, Object data){
		this.packageid = UUID.randomUUID();
		this.username = username;
		switch (type)
		{
			case MESSAGE:
				this.type = MESSAGE;
				this.message = data.toString();
				break;				
			case COMMAND: 
				this.type = COMMAND;
				this.command = (int) data;
				break;
			case STATUS:
				this.type = STATUS;
				this.status = (int) data;
				break;
			case OBJECT:
				this.type = OBJECT;
				this.object = data;				
			default:
				break;
		}
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public int getType(){
		return this.type;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public void setCommand(int command){
		this.command = command;
	}
	
	public int getCommand(){
		return this.command;
	}

	public void setObject(Object object){
		this.object = object;
	}
	
	public Object getObject(){
		return this.object;
	}
	
	public UUID getPackageID(){
		return this.packageid;
	}
	
	public String toString(){
		String returnString = "";
		returnString += "[UUID:"	+ (this.packageid 		!= null ? this.packageid:		"#");
		returnString += "|Status:"	+ (this.status 			!= INITIALIZE ? this.status:	"#");
		returnString += "|Type:"	+ (this.type 			!= INITIALIZE ? this.type:		"#");
		returnString += "|Username:"+ (this.username 		!= null ? this.username:		"#");
		returnString += "|Message:"	+ (this.message 		!= null ? this.message:			"#");
		returnString += "|Command:"	+ (this.command 		!= INITIALIZE ? this.command:	"#");
		returnString += "|Object:"	+ (this.object 			!= null ? this.object.toString():	"#")+"]";
		return returnString;
	}

}
