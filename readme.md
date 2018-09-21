## Description
The purpose of this program is to do bulk changes in the group authorizations and permissions. Can be useful for example after an upgrade to V12 whene AWI permissions have to be given to the users.

## Usage

The app expects the config.json to be present in the same directory as the binary. Then you can start it using:

java -jar AddGroupPA [parameters]

### Following options are available:
```
 -c <arg>   Path to config
 -h         Help
 -s         Simulate - dry run
```

## Configuration

There are following sections in the config json:
- credentials - connection to the Automic system
	- hostname
	- port
	- client - array of all clients that should be processed
	- user
	- password - if not provided in the config file you will be asked at runtime
	- department
	- language
- gp - group permissions setup, where you can define the privileges and authorizations for every group
	- GROUP_NAME
		- privileges - array containing the privileges should be added using the naming from the API documentation
		- authorizations - array containing authorizations that shoud be added in the following format (semicolon separated):
			- [TYPE];
			- [NAME WILDCARD];
			- [READ YES = 1 / NO = 0 ];
			- [WRITE YES = 1 / NO = 0];
			- [EXECUTE YES = 1 / NO = 0]
			- [DELETE YES = 1 / NO = 0];
			- [CANCEL YES = 1 / NO = 0];
			- [STATISTICS YES = 1 / NO = 0];
			- [REPORT YES = 1 / NO = 0];
			- [MODIFY AT RUN TIME YES = 1 / NO = 0];
			
			
### Example config

```
{
"credentials":{
	"hostname":"127.0.0.1",
	"port":"2217",
	"client":[
	"100"
	],
	"user":"AUTOMIC",
	"password":"",
	"department":"AUTOMIC",
	"language":"E"
	},
"gp": {
		"ADMIN": {
		"privileges":[
				"ECC_ADMINISTRATION", 
				"ECC_PROCESS_MONITORING",
				"ECC_PROCESS_DEVELOPMENT",
				"ECC_DASHBOARDS",
				"ECC_MESSAGES",
				"ECC_PROCESS_AUTOMATION"
		],
		"authorizations":["DASH;A??_D_*;1;1;1;1;1;1;1;1"]
		},
		"USERS": {
		"privileges":[
				"ECC_PROCESS_MONITORING",
				"ECC_PROCESS_DEVELOPMENT",
				"ECC_DASHBOARDS"
		],
		"authorizations":["DASH;A??_D_*;1;1;1;1;1;1;1;1"]
		}
	}
	
}

```



### Possible privileges:

###### ACCESS_NOFOLDER
Access to "No Folder".
###### ACCESS_RECYCLE_BIN
Access to Recycle Bin.
###### ACCESS_SYSTEM_OVERVIEW
Access to System Overview.
###### ACCESS_TRANSPORT_CASE
Access to Transport Case.
######A UTHORIZATIONS_OBJECT_LEVEL
Deal with authorizations at object level.
###### AUTO_FORECAST
Access to Auto Forecast.
###### BACKEND_VARIABLE
Create and modify Backend variables.
###### CHANGE_SYSTEM_STATUS
Change system status (STOP/GO).
###### CREATE_DIAGNOSTIC_INFO
Create diagnostic information.
###### ECC_ACCESS_ANALYTICS
ECC: Access to Analytics.
###### ECC_ADMINISTRATION
ECC: Administration.
###### ECC_ANALYTICS_ALL_CLIENTS
Access to analytics for all clients
###### ECC_DASHBOARDS
ECC: Access to Dashboards.
###### ECC_DECISION_AUTOMATION
ECC: access to Decision Automation.
###### ECC_MANAGE_SLA_AND_BU
Deprecated. 
###### ECC_MESSAGES
ECC: Access to Messages.
###### ECC_PREDICTIVE_ANALYSIS
ECC: Access to Predictive Analysis.
###### ECC_PROCESS_AUTOMATION
ECC: access to Process Automation.
###### ECC_PROCESS_DEVELOPMENT
ECC: access to Decision Automation.
###### ECC_PROCESS_MONITORING
ECC: access to Process Monitoring.
###### ECC_SERVICE_LEVEL_GOVENOR
ECC: access to Service Level Govenor.
###### ENABLE_FORCE_MEMORY_TRACE
Enable forced trace memory dump.
###### EXECUTE_ZERO_DOWNTIME_UPGRADE
Execute Zero Downtime Upgrades.
###### FAVORITES_USER_GROUP
Manage favorites on UserGroupLevel.
###### FILE_TRANSFER_WITHOUT_USERID
FileTransfer: Start without User ID.
###### FILEEVENTS_WITHOUT_LOGIN
FileEvents: Start without Login object specified.
###### ILM_ACTIONS
ILM actions.
###### LOGON_CALL_API
Logon via CallAPI.
###### MESSAGES_ADMINISTRATORS
View messages to administrators.
###### MESSAGES_OWN_CLIENT
View all messages from own client.
###### MESSAGES_OWN_GROUP
View messages from own UserGroup.
###### MESSAGES_SECURTY
View security messages.
###### MODIFY_STATUS_MANUALLY
Modify the status of a task manually.
###### RESET_OPEN_FLAG
Object properties: Allow manual reset of 'Edit Hint'.
###### SAP_CRITERIA_MANAGER
SAP Criteria Manager.
###### SELECTIVE_STATISTICS
Access to selective statistics.
###### SERVER_USAGE_ALL_CLIENTS
View server usage of all clients.
###### SQL_VARIABLE
Create and modify SQL internal variables.
###### TAKE_OVER_TASK
Take Over Task.
###### VERSION_MANAGEMENT
Access to Version Management folder.
###### WORK_IN_RUNBOOK_MODE
Work in Runbook Mode.

