

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.uc4.communication.Connection;

import automic.utils.Config;
import automic.utils.ConnectionManager;

public class Main {

	
	public static void main(String argv[]) throws Exception {
		
		//Build cli options
		Options options = new Options();

		// add options
		options.addOption("s", false, "Simulate - dry run");
		options.addOption("h", false, "Help");
		options.addOption("c", true, "Path to config");
		
		
		// parse the args
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, argv);
		
		
		// Print help 
		HelpFormatter formatter = new HelpFormatter();
		
		if (cmd.hasOption('h')) {
			formatter.printHelp( "AddGroupPerms", options );
            System.exit(0);
        }
		
		// Dry run ??
		boolean simulate = false;
		if (cmd.hasOption('s')) {
			simulate = true; 
		}
		
		
		// Get config 
		Config cfg = new Config();
		
		String filename = cmd.getOptionValue("c");
		System.out.println(filename);

		if(filename == null) {
		
			// Default path 
			filename = System.getProperty("user.dir");
			filename = filename+File.separator+"config.json";
		} 
		System.out.println("Openinig configuration: "+filename);
		
		cfg.getCredentialsFromFile(filename);
		
		for(Integer client : cfg.ClientArray) {
		
			System.out.println("Attempting to connect to: "+cfg.Hostname);
		
			Connection conn = new ConnectionManager().authenticate(
					cfg.Hostname, cfg.Port, client, cfg.User, cfg.Department, cfg.Password, cfg.Language);
			
			System.out.println("Connected to: "+ conn.getSessionInfo().getSystemName()+":"+client.toString());
			
			
			ParsePermissions.addPermissions(conn,filename, simulate);
			
			//End
			System.out.println("Closing connection");
			conn.close();
		}
	}
}
