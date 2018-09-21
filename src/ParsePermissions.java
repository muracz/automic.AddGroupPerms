import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.uc4.api.UC4ObjectName;
import com.uc4.api.objects.UC4Object;
import com.uc4.api.objects.UserGroup;
import com.uc4.api.objects.UserPrivileges;
import com.uc4.api.objects.UserPrivileges.Privilege;
import com.uc4.api.objects.UserRight;
import com.uc4.communication.Connection;
import com.uc4.communication.requests.CloseObject;
import com.uc4.communication.requests.OpenObject;
import com.uc4.communication.requests.SaveObject;


class ParsePermissions {
	
	public static void addPermissions(Connection conn, String filename, boolean simulate) throws IOException, FileNotFoundException, ParseException {
		
		JSONParser parser = new JSONParser();
		Object object = parser.parse(new FileReader(filename));
		
		//convert Object to JSONObject
		JSONObject jsonObject = (JSONObject)object;
		JSONObject gp = (JSONObject)jsonObject.get("gp");
		boolean SaveObject;
		
		
		
		if (simulate) {
			System.out.println("Running a DRY RUN only. No changes will be made!");
		}
		for(Iterator<?> groups = gp.keySet().iterator(); groups.hasNext();) {
			
			String group = groups.next().toString();
			//System.out.println("List of permissions for group: "+group);
			
			// Open the group object
			UC4ObjectName myGrp = new UC4ObjectName(group);
			OpenObject openObject = new OpenObject(myGrp);
			conn.sendRequestAndWait(openObject);
			if(openObject.getMessageBox() != null){
				System.out.println("Error:" + openObject.getMessageBox());
			}else{
				
				
				UC4Object obj = openObject.getUC4Object();
				
				UserGroup Grp = (UserGroup) obj; 
				SaveObject = false; 
				
				System.out.println("PROCESSING: " + Grp.getName());
				System.out.println("--------------------------");
				
				// Get data from the config
				JSONObject g = (JSONObject) gp.get(group);
				
				JSONArray privileges = (JSONArray) g.get("privileges");
				JSONArray authorizations = (JSONArray) g.get("authorizations");
	      	  
	     	   for (int i = 0; i < privileges.size(); i++) {
	     		   String p = privileges.get(i).toString();
	     		   
	     		  UserPrivileges.Privilege priv =  UserPrivileges.Privilege.valueOf(p);	     		   
	     		   if (! Grp.privileges().isPrivilegeSet(priv)) {
	     			 
	     			   System.out.println(p+ " - is not set, setting");
	     			   if (! simulate ) {
	     				   Grp.privileges().setPrivilege(priv, true);
	     				   SaveObject = true; 
	     			   }
	     		   }
	     	   	}
	     	   for (int i = 0; i < authorizations.size(); i++) {
	    		   String a = authorizations.get(i).toString();
	    		   
	     		  UserRight auth =  parseAuthInput(a);
	     		  if ( ! groupAuthPresent(Grp, auth)) {
	     			 System.out.println("Adding: "+auth.getName()+ " of type: "+ auth.getType().toString());
	     			if (! simulate ) {
	     				Grp.authorizations().addRight(auth);
	     				SaveObject=true;
	     			}
	     		  }
	     		  
	     		  
	     		  
	    	   }
	     	   
	     	   
	     	   System.out.println("The following privileges are set for " + group );
	     	  for (Privilege pv : UserPrivileges.Privilege.values()) {
 				  if (Grp.privileges().isPrivilegeSet(pv)) {
 				   System.out.println(pv);
 				  } 
 			   }
	     	   
	     	   
	     	   if (SaveObject) {
	     		   System.out.printf("Saving object...");	
	     		   SaveObject req = new SaveObject(Grp);
					conn.sendRequestAndWait(req);
					if(req.getMessageBox() != null){
						System.out.println("Error:" + req.getMessageBox());
					} else {
						System.out.printf("success!%n");
					}
	     	   }
	     	   
	     	   // Closing object
	     	   CloseObject req = new CloseObject(Grp);
	     	  conn.sendRequestAndWait(req);
			}
		}     
		
		
	}
	

	/** Parses an input semicolon separated string and turns it ino UserRight object **/ 
	private static UserRight parseAuthInput(String input) { 
		
		// Explode and verify the input 
		List<String> inList = (List<String>)Arrays.asList(input.split(";"));
		
		if (inList.size() != 10 ) {
			System.out.println("Error: Incorrect amount of paramters for auth! " + input);
			return null;
		}
		
		String type = inList.get(0);
		String name = inList.get(1);
		boolean isRead = toBool(inList.get(2));
		boolean isWrite = toBool(inList.get(3));
		boolean isExec = toBool(inList.get(4));
		boolean isDel = toBool(inList.get(5));
		boolean isCan = toBool(inList.get(6));
		boolean isS = toBool(inList.get(7));
		boolean isP = toBool(inList.get(8));
		boolean isM = toBool(inList.get(9));
		
		
		UserRight ur = new UserRight(); 
		
		ur.setType(UserRight.Type.valueOf(type));
		ur.setName(name);
		ur.setRead(isRead);
		ur.setWrite(isWrite);
		ur.setExecute(isExec);
		ur.setDelete(isDel);
		ur.setCancel(isCan);
		ur.setStatistics(isS);
		ur.setReport(isP);
		ur.setModifyAtRuntime(isM);	
		
		return ur; 
	}
	
	
	/** Transform string "0" and "1" to bool **/
	private static boolean toBool(String in) {
		boolean out = false;
		
		Integer i = Integer.parseInt(in);
		
		if(i == 1) {
			out = true;
		} 
		
		return out; 
	}
	
	/** Returns true if a group already has an authorization for a particual name wildcard and 
	 * of the same type **/
	private static boolean groupAuthPresent(UserGroup group, UserRight auth) {
		boolean result = true; 
		
		Iterator<UserRight> authorizations = group.authorizations().iterator();
		
		while (authorizations.hasNext()) {
			UserRight authorization = authorizations.next();
			
			if (authorization.getName().equals(auth.getName()) && authorization.getType().equals(auth.getType())) {
				System.out.println("The group has already a definition for "+ auth.getType().toString()+" with name "+ auth.getName()+ "...skipping");
				result = true;
			} else {
				result = false;
			}
				
		}
		
		return result; 
	}
	
	
	
}
