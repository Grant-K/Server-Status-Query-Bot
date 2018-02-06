package Query.Bot;

import java.net.InetSocketAddress;
import java.nio.file.FileSystems;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.security.auth.login.LoginException;
import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourcePlayer;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.*;

public class App extends ListenerAdapter
{
	public static String cwd = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
	public static String confPath = (cwd + "\\LNbot\\config.txt");
	public String statusRoom;
	public FileReader fr = null;
  	public BufferedReader br = null;
  	public FileWriter fw = null;
  	public PrintWriter pw = null;
  	public SourceServer serverInfo = null;
  	public CompletableFuture<List<SourcePlayer>> playerInfoFuture = null;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, LoginException, IOException
    {
		System.out.println(cwd);
		System.out.println(confPath);
		JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("NDA4ODYyODcwMzcwMjU0ODQ4.DVp4mQ.58Qn8wFhcIFGFwR5Ek032N7Xilo").buildBlocking();
    	jdaBot.addEventListener(new App());
    	System.out.println("Bot Online");
    	App driver = new App();
    	driver.setStatusRoom();
    }
	
	public void setStatusRoom()
	{
	  	statusRoom = "servers-status";
		try {
			fr = new FileReader(confPath);
			br = new BufferedReader(fr);
			String line = br.readLine();
			if(line != null)
				statusRoom = line;
			br.close();
		} catch (FileNotFoundException e2) {
			System.out.println("Error!");
		} catch (IOException e1) {
			System.out.println("Error!");
		}
		
	}
	
	public void setStatusRoom(String arg)
	{
	  	statusRoom = "servers-status";
	  	try {
			fw = new FileWriter(confPath);
			pw = new PrintWriter(fw);
			pw.println(arg);
			pw.close();
		} catch (IOException e1) {
		}
	  	
		try {
			fr = new FileReader(confPath);
			br = new BufferedReader(fr);
			String line = br.readLine();
			if(line != null)
				statusRoom = line;
			br.close();
		} catch (FileNotFoundException e2) {
			System.out.println("Error!");
		} catch (IOException e1) {
			System.out.println("Error!");
		}
	}
	
	public void getStatusRoom() 
	{
		statusRoom = "servers-status";
		try {
			fr = new FileReader(confPath);
			br = new BufferedReader(fr);
			String line = br.readLine();
			if(line != null)
				statusRoom = line;
			br.close();
		} catch (FileNotFoundException e2) {
			System.out.println("Error!");
		} catch (IOException e1) {
			System.out.println("Error!");
		}
	}
	
	public boolean queryServer(String ip, int port)
	{
		@SuppressWarnings("resource")
    	SourceQueryClient sourceQueryClient = new SourceQueryClient();
        InetSocketAddress serverAddress = new InetSocketAddress(ip, port);
        try{
        		serverInfo = sourceQueryClient.getServerInfo(serverAddress).get();
        		playerInfoFuture = sourceQueryClient.getPlayers(serverAddress);
            	playerInfoFuture.whenComplete((players, playerError) -> {
            	
            });
            	return true;
        	} catch (ExecutionException e1)
        	{
        		return false;
        	} catch (InterruptedException e1) {
        		return false;
			}
	}
	
    public void onMessageReceived(MessageReceivedEvent e) 
    {
    	if (e.getAuthor().isBot()) return;
        //Obtains properties of the received message
        Message objMsg = e.getMessage();
        MessageChannel objChannel = e.getChannel();
        User objUser = e.getAuthor();
        String channelName = objChannel.getName();
        String[] messageParts = objMsg.getContentRaw().split("\\s");
        String command = messageParts[0];
        String arg = null;
        if(messageParts.length > 1)
        	{
        		arg = messageParts[1];
        		for(int i = 2; i < messageParts.length; i++)
        			arg = arg + messageParts[i];
        	}
    	setStatusRoom();
        if(channelName.equals(statusRoom) && (objUser.isBot()) == false)
        	{
	        	System.out.println("Message Recived! Contents is as follows:");
	        	System.out.println(objMsg.getContentRaw());
	        	System.out.println("Author is: " + objUser.getName());
        	}
        String SpaceEngineersip = "107.150.63.18";
        int SpaceEngineersPort = 27016;
    	boolean SpaceEngineersup = queryServer(SpaceEngineersip, SpaceEngineersPort);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        if(command.substring(0,1).equals("^") && (objUser.isBot()) == false)
        	{
        		System.out.println("Mesage initation character entered! Reply is as follows!");
        		if (command.equals("^ServerStatus") && channelName.equals(statusRoom))
        		{
        			System.out.println(arg + command);
        			if(arg == null || arg == "" || arg == " " || arg.toLowerCase().equals("all") || arg.toLowerCase().equals("both"))
        			{
        				if(SpaceEngineersup)
	        				{
		    	    			objChannel.sendMessage("The SpaceEngineers Server is Online!").complete();
		    	    			System.out.println("The SpaceEngineers Server is Online!");
		    	    			objChannel.sendMessage("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers()).complete();
		    	    			System.out.println("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers());
		    	    			List<SourcePlayer> players = null;
								try {
									players = playerInfoFuture.get();
								} catch (InterruptedException e1) {
									System.out.println("Error!");
								} catch (ExecutionException e1) {
									System.out.println("Error!");
								}
								if(players.size() >= 1)
								{
									objChannel.sendMessage("The Online Players Are:").complete();
									System.out.println("The Online Players Are:");
								}
								 int playersOnline = players.size();
		    	    			 for(int i = 0; i < playersOnline; i++)
		    	    			 {
		    	    				 System.out.println(i);
		    	    				 String playerName = players.get(i).getName();
		    	    				 String playersTimeOnline = numberFormat.format((players.get(i).getDuration()) / 60.0) + " (min)";
		    	    				 objChannel.sendMessage(playerName + "\t Time Online: " + playersTimeOnline ).queue();
		    	    			 	 System.out.println(playerName + "\t Time Online: " + playersTimeOnline);
		    	    			 }
		    	    		}
        				         else 
        				         {
			    	    			 objChannel.sendMessage("The  SpaceEngineers Server is Offline!").complete();
			    	    			 System.out.println("The  SpaceEngineers Server is Offline!");
		    	    			 }
		        	 }
        			 else if(arg.toLowerCase().equals("spaceengineers"))
		        	 {
        				 if(SpaceEngineersup)
		        		 {
        					 objChannel.sendMessage("The SpaceEngineers Server is Online!").complete();
						     System.out.println("The SpaceEngineers Server is Online!");
						     objChannel.sendMessage("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers()).complete();
						     System.out.println("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers());
						     List<SourcePlayer> players = null;
							 try 
							 {
								 players = playerInfoFuture.get();
							 } 
							 catch (InterruptedException e1) 
							 {
								 System.out.println("Error!");
							 } 
							 catch (ExecutionException e1) 
							 {
								 System.out.println("Error!");
							 }
							 if(players.size() >= 1) 
							 {
								 objChannel.sendMessage("The Online Players Are:").complete();
								 System.out.println("The Online Players Are:");
							 }
						     for(int i = 0; i < players.size(); i++)
						     {
						    	 objChannel.sendMessage(players.get(i).getName() + "\t Time Online: " + numberFormat.format((players.get(i).getDuration()) / 60.0) + " (min)").complete();
						     	System.out.println(players.get(i).getName() + "\t Time Online: " + numberFormat.format((players.get(i).getDuration()) / 60.0) + " (min)");
						     }
		        		  }
		        		  else 
		        		  {
		        			  objChannel.sendMessage("The SpaceEngineers Server is Offline!").complete();
		        			  System.out.println("The  SpaceEngineers Server is Offline!");
		        		  }
		        	 }
		         }
		         else if (command.equals("^ServerStatus") && (arg != null && arg != "" && arg != " "))
		         {
		        	 objChannel.sendMessage("Please enter the command with the server you wish to check afterwards (^ServerStatus SpaceEngineers)").complete();
		        	 System.out.println("Please enter the command with the server you wish to check afterwards (^ServerStatus SpaceEngineers)");
		         }
		         else
		         {
		        	 if(command.toLowerCase().equals("^help"))// && rolePos >= 32)
			         {
		        		 objChannel.sendMessage("Currently avaliable command(s) are: ^ServerStatus (ServerName) which checks to see if the desired server is online.").complete();
			        	 System.out.println("Currently avaliable command(s) are: ^ServerStatus (ServerName) which checks to see if the desired server is online.");
			         }
			         if(command.toLowerCase().equals("^setstatuschannel"))
			         {
			        	 if(arg == null)
				         {
			        		 objChannel.sendMessage("Please enter the command with the channel you wish to set for server status afterswords! I.E. (^SetStatusChannel server-status)").complete();
				        	 System.out.println("Please enter the command with the channel you wish to set for server status afterswords! I.E. (^SetStatusChannel server-status)");
				         }
			        	 else
			        	 {			
			        		 setStatusRoom(arg);
			        		 objChannel.sendMessage("Server status channel is now set to: " + statusRoom).complete();
			        		 System.out.println("Server status channel is now set to: " + statusRoom);
			        	 }
			          }
			          else if(command.toLowerCase().equals("^getstatuschannel"))
			          {
			        	  getStatusRoom();
			        	  objChannel.sendMessage("The server status channel is curently set to: " + statusRoom).complete();
			    		  System.out.println("The server status channel is curently set to: " + statusRoom);
			          }
	        		  else
	        		  {
	        			  objChannel.sendMessage("Please do ^help for help!").complete();
	        			  System.out.println("Please do ^help for help!");
			          }
		           }
        	 }
    	}
}
